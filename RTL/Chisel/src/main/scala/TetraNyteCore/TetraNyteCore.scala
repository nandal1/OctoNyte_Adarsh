// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package TetraNyte

import chisel3._
import chisel3.util._
import TetraNyte.PipelineHelpers._
import TetraNyte.PipelineHelpers.updatePipelineStage

/** 
  * TetraNyteCore implements a 4-stage pipeline:
  *   1. IF (Fetch)
  *   2. DEC/RF (Decode + Register File Read)
  *   3. EX (Execute)
  *   4. MEM/WB (Memory Access and Write-Back)
  *
  * It uses a multithreaded register file implemented as a Vec of registers 
  * (RegFileMT2R1WVec).
  */
class TetraNyteCore extends Module {
  val io = IO(new Bundle {
    // Data memory interface
    val memAddr  = Output(UInt(32.W))
    val memWdata = Output(UInt(32.W))
    val memRdata = Input(UInt(32.W))
    val memWe    = Output(Bool())  // Store enable

    // Instruction memory write interface (for testbench initialization)
    val instrWriteEnable = Input(Bool())
    val instrWriteAddr   = Input(UInt(10.W))  // e.g. depth=1024 => 10 bits
    val instrWriteData   = Input(UInt(32.W))

    //For print statements
    val debug = Input(Bool())
  })

  // Number of threads
  val numThreads = 4
  val threadBits = log2Ceil(numThreads)

  // Round-robin pointer for thread scheduling.
  val currentThread = RegInit(0.U(threadBits.W))
  currentThread := currentThread + 1.U

  // Per-thread PC register.
  val pcRegs = RegInit(VecInit(Seq.fill(numThreads)(0.U(32.W))))

  // Instruction memory (synchronous).
  val instrMem = SyncReadMem(1024, UInt(32.W))
  when(io.instrWriteEnable) {
    instrMem.write(io.instrWriteAddr, io.instrWriteData)
  }

  // Instantiate the multithreaded register file as a Vec of registers.
  val regFile = Module(new RegFileMT2R1WVec(width = 32, depth = 32, numThreads = numThreads))

  // A minimal ALU module.
  class AluIO extends Bundle {
    val a   = Input(UInt(32.W))
    val b   = Input(UInt(32.W))
    val fn  = Input(UInt(5.W)) // 0 = ADD, 1 = SUB, etc.
    val out = Output(UInt(32.W))
  }
  val alu = Module(new Module {
    val io = IO(new AluIO)
    import io._
    out := 0.U
    switch(fn) {
      is(0.U) { out := a + b }  // ADD
      is(1.U) { out := a - b }  // SUB
      // Additional operations can be added here.
    }
  })

  //==========================================================================
  // Pipeline Registers: one for each stage.
  // We use the full PipelineRegBundle for all stages.
  // For a 4-stage pipeline, we need:
  //   ifStage, decStage, exStage, and memStage.
  //==========================================================================
  val ifStage  = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val decStage = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val exStage  = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val memStage = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))

  //==========================================================================
  // 1) IF Stage: Instruction Fetch
  //==========================================================================
  // Use WireInit so that all fields are initialized.
  val ifWire = WireInit(0.U.asTypeOf(new PipelineRegBundle))
  ifWire.threadId := currentThread
  ifWire.valid    := true.B
  ifWire.pc       := pcRegs(currentThread)
  ifWire.instr    := instrMem.read(pcRegs(currentThread) >> 2)
  // (Other fields remain at their default zero)

  // Update PC: naive next-PC = PC + 4
  pcRegs(currentThread) := pcRegs(currentThread) + 4.U

  // Latch IF stage for the current thread.
  ifStage(currentThread) := ifWire

  // Push IF stage into the DEC/RF stage.
  updatePipelineStage(currentThread, ifStage, decStage)

  //==========================================================================
  // 2) DEC/RF Stage: Decode the instruction and perform register file read.
  //==========================================================================
  val decWire = WireInit(0.U.asTypeOf(new PipelineRegBundle))
  decWire := decStage(currentThread) // Start with the data from IF.

  // Basic decode: check opcode.
  val opcode = decWire.instr(6, 0)
  when(opcode === "b0110011".U) { // R-type
    decWire.isALU := true.B
  } .elsewhen(opcode === "b0000011".U) { // Load
    decWire.isLoad := true.B
  } .elsewhen(opcode === "b0100011".U) { // Store
    decWire.isStore := true.B
  }
  // Set register file addresses.
  decWire.rs1 := decWire.instr(19, 15)
  decWire.rs2 := decWire.instr(24, 20)
  decWire.rd  := decWire.instr(11, 7)

  // Latch the decode results.
  decStage(currentThread) := decWire

  // Now perform the register file read.
  regFile.io.threadID := currentThread
  regFile.io.src1     := decWire.rs1
  regFile.io.src2     := decWire.rs2
  // The register file is implemented as a Vec(Reg(...)), so its read is combinational.
  decWire.rs1Data := regFile.io.src1data
  decWire.rs2Data := regFile.io.src2data

  // Latch the updated results.
  decStage(currentThread) := decWire

  // Push DEC/RF stage into the EX stage.
  updatePipelineStage(currentThread, decStage, exStage)

  //==========================================================================
  // 3) EX Stage: Execute (ALU operations, address generation)
  //==========================================================================
  val exWire = WireInit(0.U.asTypeOf(new PipelineRegBundle))
  exWire := exStage(currentThread) // Get inputs from DEC/RF stage.

  // Perform ALU operation for ALU-type instructions.
  alu.io.a  := exWire.rs1Data
  alu.io.b  := exWire.rs2Data  // For simplicity, using rs2Data; add logic for immediates if needed.
  alu.io.fn := exWire.aluOp  // Control signal should have been set during decode.

  exWire.aluResult := alu.io.out

  // For load/store instructions, compute effective address.
  when(exWire.isLoad || exWire.isStore) {
    exWire.memAddr  := exWire.rs1Data + exWire.imm
    exWire.memWdata := exWire.rs2Data
  }

  // Latch EX stage results.
  exStage(currentThread) := exWire

  // Push EX stage into the MEM/WB stage.
  updatePipelineStage(currentThread, exStage, memStage)

  //==========================================================================
  // 4) MEM/WB Stage: Memory Access and Write-Back.
  //==========================================================================
  val memWire = WireInit(0.U.asTypeOf(new PipelineRegBundle))
  memWire := memStage(currentThread)

  // Memory access.
  io.memAddr  := memWire.memAddr
  io.memWdata := memWire.memWdata
  io.memWe    := memWire.isStore && memWire.valid

  // Assume for loads that data is returned in the same cycle.
  memWire.memRdata := io.memRdata

  // Write-back: For a load, write memRdata; for an ALU instruction, use aluResult.
  val wbData = Mux(memWire.isLoad, memWire.memRdata, memWire.aluResult)
  regFile.io.dst1     := memWire.rd
  regFile.io.dst1data := wbData
  regFile.io.wen      := memWire.valid && (memWire.rd =/= 0.U) &&
                         (memWire.isLoad || memWire.isALU)

  // Latch MEM/WB stage results.
  memStage(currentThread) := memWire
  // (No further stage to update.)

  //==========================================================================
  // Debug Printouts
  //==========================================================================
  // printf("--- Cycle %d, Thread %d ---\n", RegNext(RegNext(0.U)), currentThread)
  // for (t <- 0 until numThreads) {
  //   printf(p"  T$t IF   = ${ifStage(t)}\n")
  //   printf(p"  T$t DEC  = ${decStage(t)}\n")
  //   printf(p"  T$t EX   = ${exStage(t)}\n")
  //   printf(p"  T$t MEM  = ${memStage(t)}\n\n")
  // }

  // printf("--- Cycle %d ---\n", RegNext(RegNext(0.U)))
  // for (t <- 0 until numThreads) {
  //   printf("  T%d IF   = { pc: 0x%x, instr: 0x%x }\n", t.U, ifStage(t).pc, ifStage(t).instr)
  //   printf("  T%d DEC  = { rs1: 0x%x, rs2: 0x%x, rd: 0x%x }\n", t.U, decStage(t).rs1, decStage(t).rs2, decStage(t).rd)
  //   printf("  T%d EX   = { aluResult: 0x%x, memAddr: 0x%x }\n", t.U, exStage(t).aluResult, exStage(t).memAddr)
  //   printf("  T%d MEM  = { memWdata: 0x%x, memRdata: 0x%x }\n\n", t.U, memStage(t).memWdata, memStage(t).memRdata)
  // }

  // Compute the active thread for each stage.
  // when(io.debug)
  // {
  //   val activeIF  = currentThread  // current thread fetches
  //   val activeDEC = (currentThread + (numThreads.U - 1.U)) % numThreads.U
  //   val activeEX  = (currentThread + (numThreads.U - 2.U)) % numThreads.U
  //   val activeMEM = (currentThread + (numThreads.U - 3.U)) % numThreads.U

  //   // Print debug information only for the active pipeline register in each stage.
  //   printf("Cycle %d:\n", RegNext(RegNext(0.U)))
  //   printf("  IF  (T%d): { pc: 0x%x, instr: 0x%x }\n",
  //         activeIF, ifStage(activeIF).pc, ifStage(activeIF).instr)
  //   printf("  DEC (T%d): { pc: 0x%x, rs1: 0x%x, rs2: 0x%x, rd: 0x%x }\n",
  //         activeDEC, decStage(activeDEC).pc, decStage(activeDEC).rs1, decStage(activeDEC).rs2, decStage(activeDEC).rd)
  //   printf("  EX  (T%d): { aluResult: 0x%x, memAddr: 0x%x }\n",
  //         activeEX, exStage(activeEX).aluResult, exStage(activeEX).memAddr)
  //   printf("  MEM (T%d): { memWdata: 0x%x, memRdata: 0x%x }\n\n",
  //         activeMEM, memStage(activeMEM).memWdata, memStage(activeMEM).memRdata)
  // }
}
