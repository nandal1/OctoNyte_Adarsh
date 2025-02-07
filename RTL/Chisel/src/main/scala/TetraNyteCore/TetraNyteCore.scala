// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package TetraNyte

import chisel3._
import chisel3.util._
import TetraNyte.PipelineHelpers._
import TetraNyte.PipelineHelpers.updatePipelineStage

/** 
  * TetraNyteCore implements a 4-stage pipeline:
  * 1. IF (Fetch)
  * 2. DEC/RF (Decode + Register File Read)
  * 3. EX (Execute)
  * 4. MEM/WB (Memory Access and Write-Back)
  *
  * It uses a multi-threaded 2-read/1-write register file implemented as a Vec of registers
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
    val instrWriteAddr   = Input(UInt(10.W))  // e.g. 1024 = 2^10 depth
    val instrWriteData   = Input(UInt(32.W))
  })

  // Number of threads
  val numThreads = 4
  val threadBits = log2Ceil(numThreads)

  // Round-robin pointer for scheduling threads
  val currentThread = RegInit(0.U(threadBits.W))
  currentThread := currentThread + 1.U

  // Per-thread program counter (PC)
  val pcRegs = RegInit(VecInit(Seq.fill(numThreads)(0.U(32.W))))

  // Instruction memory (synchronous)
  val instrMem = SyncReadMem(1024, UInt(32.W))
  when(io.instrWriteEnable) {
    instrMem.write(io.instrWriteAddr, io.instrWriteData)
  }

  // Multi-threaded 2-read/1-write register file implemented as a vector of registers.
  // (Assume RegFileMT2R1WVec is defined with an I/O interface similar to the SRAM version.)
  val regFile = Module(new RegFileMT2R1WVec(width = 32, depth = 32, numThreads = numThreads))

  // A minimal ALU module (for example purposes)
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
      // Additional operations can be added as needed.
    }
  })

  //==========================================================================
  // Pipeline register definitions (one for each stage)
  // We use a "full" PipelineRegBundle that carries all possible fields.
  // For a 4-stage pipeline we need:
  //   ifStage, decStage, exStage, memStage.
  //==========================================================================
  val ifStage  = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val decStage = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val exStage  = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val memStage = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))

  //==========================================================================
  // 1) IF Stage: Instruction Fetch
  //==========================================================================
  val ifWire = Wire(new PipelineRegBundle)
  ifWire.threadId := currentThread
  ifWire.valid    := true.B
  ifWire.pc       := pcRegs(currentThread)
  ifWire.instr    := instrMem.read(pcRegs(currentThread) >> 2)
  // (Other fields remain at default zero)

  // Update PC: naive next-PC = PC + 4
  pcRegs(currentThread) := pcRegs(currentThread) + 4.U

  // Latch IF stage for the current thread
  ifStage(currentThread) := ifWire

  // Push IF stage into the DEC/RF stage
  updatePipelineStage(currentThread, ifStage, decStage)

  //==========================================================================
  // 2) DEC/RF Stage: Decode the instruction and perform register file read
  //==========================================================================
  val decWire = Wire(new PipelineRegBundle)
  decWire := decStage(currentThread) // Start with the contents coming from IF

  // Decode: determine instruction type and set control signals.
  val opcode = decWire.instr(6, 0)
  when(opcode === "b0110011".U) { // R-type
    decWire.isALU := true.B
  } .elsewhen(opcode === "b0000011".U) { // Load
    decWire.isLoad := true.B
  } .elsewhen(opcode === "b0100011".U) { // Store
    decWire.isStore := true.B
  }
  // (Additional decoding as required can be added here)

  // Set register addresses from the instruction fields.
  decWire.rs1 := decWire.instr(19, 15)
  decWire.rs2 := decWire.instr(24, 20)
  decWire.rd  := decWire.instr(11, 7)

  // Latch the updated decode information into the decStage register.
  decStage(currentThread) := decWire

  // Now perform register file read.
  regFile.io.threadID := currentThread
  regFile.io.src1     := decWire.rs1
  regFile.io.src2     := decWire.rs2
  // (Write port will be driven later in the MEM/WB stage.)

  // Because regFile is implemented as a Vec(Reg(...)), assume its read ports are combinational.
  // Thus, we can directly capture the register data in decWire.
  decWire.rs1Data := regFile.io.src1data
  decWire.rs2Data := regFile.io.src2data

  // Latch the results back into decStage.
  decStage(currentThread) := decWire

  // Push DEC/RF stage into the EX stage.
  updatePipelineStage(currentThread, decStage, exStage)

  //==========================================================================
  // 3) EX Stage: Execute (ALU operations, address generation)
  //==========================================================================
  val exWire = Wire(new PipelineRegBundle)
  exWire := exStage(currentThread)  // Get data from the previous stage

  // Perform ALU operation for ALU-type instructions.
  alu.io.a  := exWire.rs1Data
  // For simplicity, we assume ALU-type uses register-register; for load immediate,
  // one might choose between imm and rs2Data.
  alu.io.b  := exWire.rs2Data
  alu.io.fn := exWire.aluOp // Control signal set during decode

  exWire.aluResult := alu.io.out

  // For load/store, compute the effective address.
  when(exWire.isLoad || exWire.isStore) {
    exWire.memAddr  := exWire.rs1Data + exWire.imm
    exWire.memWdata := exWire.rs2Data
  }

  // Latch the EX stage results.
  exStage(currentThread) := exWire

  // Push EX stage into the MEM/WB stage.
  updatePipelineStage(currentThread, exStage, memStage)

  //==========================================================================
  // 4) MEM/WB Stage: Memory Access and Write-Back
  //==========================================================================
  val memWire = Wire(new PipelineRegBundle)
  memWire := memStage(currentThread)

  // Memory operations:
  io.memAddr  := memWire.memAddr
  io.memWdata := memWire.memWdata
  io.memWe    := memWire.isStore && memWire.valid

  // Assume that for loads, the memory returns data in the same cycle.
  memWire.memRdata := io.memRdata

  // Write-back: For load instructions, the value to write is memRdata; for ALU
  // instructions, it is aluResult.
  val wbData = Mux(memWire.isLoad, memWire.memRdata, memWire.aluResult)
  regFile.io.dst1     := memWire.rd
  regFile.io.dst1data := wbData
  regFile.io.wen      := memWire.valid && (memWire.rd =/= 0.U) &&
                         (memWire.isLoad || memWire.isALU)

  // Latch the final MEM/WB stage (no further pipeline push).
  memStage(currentThread) := memWire

  //==========================================================================
  // Debug Printouts
  //==========================================================================
  printf("--- Cycle %d, Thread %d ---\n", RegNext(RegNext(0.U)), currentThread)
  for (t <- 0 until numThreads) {
    printf(p"  T$t IF   = ${ifStage(t)}\n")
    printf(p"  T$t DEC  = ${decStage(t)}\n")
    printf(p"  T$t EX   = ${exStage(t)}\n")
    printf(p"  T$t MEM  = ${memStage(t)}\n\n")
  }
}
