package TetraNyte

import chisel3._
import chisel3.util._
import TetraNyte.PipelineHelpers._
import TetraNyte.PipelineHelpers.updatePipelineStage

class TetraNyteCore extends Module {
  val io = IO(new Bundle {
    // Single memory port for load/store
    val memAddr  = Output(UInt(32.W))
    val memWdata = Output(UInt(32.W))
    val memRdata = Input(UInt(32.W))
    val memWe    = Output(Bool())  // store enable
    // Instruction Memory
    val instrWriteEnable = Input(Bool())
    val instrWriteAddr   = Input(UInt(10.W))  // e.g. 1024 = 2^10 depth
    val instrWriteData   = Input(UInt(32.W))
  })

  // Number of threads
  val numThreads = 4
  val threadBits = log2Ceil(numThreads)

  // Round-robin pointer
  val currentThread = RegInit(0.U(threadBits.W))
  currentThread := currentThread + 1.U

  // Per-thread PC
  val pcRegs = RegInit(VecInit(Seq.fill(numThreads)(0.U(32.W))))

  // A minimal instruction memory (toy)
  val instrMem = SyncReadMem(1024, UInt(32.W))
  when (io.instrWriteEnable) {
    instrMem.write(io.instrWriteAddr, io.instrWriteData)
  }


  // Multi-threaded 2R1W regfile
  val regFile = Module(new RegFileMT2R1WSRAM(width=32, depth=32, threads=numThreads))

  // A minimal ALU example
  class AluIO extends Bundle {
    val a     = Input(UInt(32.W))
    val b     = Input(UInt(32.W))
    val fn    = Input(UInt(5.W))  // e.g. {sub, sll, sra, etc.}
    val out   = Output(UInt(32.W))
  }
  val alu = Module(new Module {
    val io = IO(new AluIO)
    import io._
    out := 0.U
    switch(fn) {
      is(0.U) { out := a + b }  // ADD
      is(1.U) { out := a - b }  // SUB
      // add more ops as needed...
    }
  })

  //==========================================================================
  // Pipeline registers for each stage (Vec of size 4 for each stage)
  //==========================================================================
  val ifStage   = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val decStage  = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val rfStage   = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val exStage   = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val memStage  = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))
  val wbStage   = RegInit(VecInit(Seq.fill(numThreads)(0.U.asTypeOf(new PipelineRegBundle))))

  //==========================================================================
  // 1) IF Stage
  //==========================================================================
  // 1a) Read & update IF logic
  val ifWire = WireDefault(ifStage(currentThread))

  // Overwrite the fields we want to set this cycle
  ifWire.threadId := currentThread
  ifWire.valid    := true.B
  ifWire.pc       := pcRegs(currentThread)
  ifWire.instr    := instrMem.read(pcRegs(currentThread) >> 2)

  // naive next PC = pc + 4
  pcRegs(currentThread) := pcRegs(currentThread) + 4.U

  // Write back into ifStage for currentThread
  ifStage(currentThread) := ifWire

  // 1b) At the end of IF stage, push to DEC stage
  updatePipelineStage(currentThread, ifStage, decStage)

  //==========================================================================
  // 2) DEC Stage
  //==========================================================================
  val decWire = WireDefault(decStage(currentThread))

  // Example decode logic
  val opcode = decWire.instr(6,0)
  when(opcode === "b0110011".U) { // R-type
    decWire.isALU := true.B
  }.elsewhen(opcode === "b0000011".U) { // load
    decWire.isLoad := true.B
  }.elsewhen(opcode === "b0100011".U) { // store
    decWire.isStore := true.B
  }
  // etc. More decode as you wish

  // Suppose we also set register addresses here
  decWire.rs1 := decWire.instr(19,15)
  decWire.rs2 := decWire.instr(24,20)
  decWire.rd  := decWire.instr(11,7)

  // Write back into decStage
  decStage(currentThread) := decWire

  // Push to RF stage
  updatePipelineStage(currentThread, decStage, rfStage)

  //==========================================================================
  // 3) RF Stage
  //==========================================================================
  val rfWire = WireDefault(rfStage(currentThread))

  // Hook up regFile read ports using the decode info
  regFile.io.threadID := currentThread
  regFile.io.src1     := rfWire.rs1
  regFile.io.src2     := rfWire.rs2
  // We'll do the write port in WB stage

  // The regFile read is synchronous => one cycle delayed inside the regFile.
  // The outputs come via internal RegNext in RegFileMT2R1WSRAM.
  rfWire.rs1Data := regFile.io.src1data
  rfWire.rs2Data := regFile.io.src2data

  rfStage(currentThread) := rfWire

  // Now push to EX
  updatePipelineStage(currentThread, rfStage, exStage)

  //==========================================================================
  // 4) EX Stage
  //==========================================================================
  val exWire = WireDefault(exStage(currentThread))

  // Minimal ALU logic: if isALU, do rs1Data op rs2Data
  alu.io.a  := exWire.rs1Data
  // If you want to handle imm for I-type, add logic here:
  alu.io.b  := exWire.rs2Data
  alu.io.fn := exWire.aluOp // e.g. 0=ADD,1=SUB

  exWire.aluResult := alu.io.out

  // You might also set memAddr here for loads/stores:
  when(exWire.isLoad || exWire.isStore) {
    exWire.memAddr  := exWire.rs1Data + exWire.imm
    exWire.memWdata := exWire.rs2Data
  }

  exStage(currentThread) := exWire

  updatePipelineStage(currentThread, exStage, memStage)

  //==========================================================================
  // 5) MEM Stage
  //==========================================================================
  val memWire = WireDefault(memStage(currentThread))

  // Single cycle memory op (toy)
  io.memAddr  := memWire.memAddr
  io.memWdata := memWire.memWdata
  io.memWe    := memWire.isStore && memWire.valid

  // Suppose read data returns in the same cycle
  memWire.memRdata := io.memRdata

  memStage(currentThread) := memWire
  updatePipelineStage(currentThread, memStage, wbStage)

  //==========================================================================
  // 6) WB Stage
  //==========================================================================
  val wbWire = WireDefault(wbStage(currentThread))

  // If it's a load or ALU instruction, write back
  val wbData = Mux(wbWire.isLoad, wbWire.memRdata, wbWire.aluResult)
  regFile.io.dst1     := wbWire.rd
  regFile.io.dst1data := wbData
  regFile.io.wen      := wbWire.valid && (wbWire.rd =/= 0.U) &&
                         (wbWire.isLoad || wbWire.isALU)

  wbStage(currentThread) := wbWire
  // There's no next stage to push to, so no updatePipelineStage call here

  //==========================================================================
  // Debug
  //==========================================================================
  printf("--- Cycle %d, Thread %d ---\n", RegNext(RegNext(0.U)), currentThread)
  for (t <- 0 until numThreads) {
    printf(p"  T$t IF   = ${ifStage(t)}\n")
    printf(p"  T$t DEC  = ${decStage(t)}\n")
    printf(p"  T$t RF   = ${rfStage(t)}\n")
    printf(p"  T$t EX   = ${exStage(t)}\n")
    printf(p"  T$t MEM  = ${memStage(t)}\n")
    printf(p"  T$t WB   = ${wbStage(t)}\n\n")
  }
}
