// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package OctoNyteCore

import chisel3._
import chisel3.util._

class OctoNyteCore(
    val numLoadUnits: Int = 1,
    val numStoreUnits: Int = 1,
    val numALUUnits: Int = 2,
    val numVectorUnits: Int = 1,
    val numThreads: Int = 8,
    val instructionWindowSize: Int = 16,
    val aluPipelineStages: Int = 2,
    val mulPipelineStages: Int = 3,
    val vecPipelineStages: Int = 4,
    val enableLogging: Boolean = false
) extends Module {
  val io = IO(new Bundle {
    val imem = new MemoryInterface
    val dmem = new MemoryInterface
    val interrupt = Input(Bool()) // Global interrupt signal
    val interruptMask = Input(Vec(numThreads, Bool())) // Mask register for interrupts
  })


  // Hardware round-robin scheduling
  val currentThread = RegInit(0.U(log2Ceil(numThreads).W))
  val pendingInterrupt = RegInit(false.B)
  currentThread := Mux(io.interrupt && io.interruptMask(currentThread), currentThread, (currentThread + 1.U) % numThreads.U)

  val pc = RegInit(VecInit(Seq.fill(numThreads)(0.U(32.W))))

  // Initialize Instruction Memory interface signals
  io.imem.address := 0.U(32.W)
  io.imem.writeData := 0.U(32.W)
  io.imem.writeEnable := false.B

  // Instruction window and issue buffer
  val instructionWindow = Reg(Vec(instructionWindowSize, UInt(32.W)))
  val issueBuffer = Reg(Vec(numThreads, Vec(instructionWindowSize, Bool()))) // Tracks which instructions are ready

  val decodeStage = Wire(new DecodeStage)
  decodeStage.rs1 := 0.U
  decodeStage.rs2 := 0.U
  decodeStage.op := 0.U
  
  val aluExecStages = Seq.fill(numALUUnits)(Module(new ALU).io)
  val mulExecStages = Seq.fill(numLoadUnits)(Module(new Multiplier).io)
  val vecExecStages = Seq.fill(numVectorUnits)(Module(new VectorUnit).io)
  val loadUnits = Seq.fill(numLoadUnits)(Module(new LoadUnit).io)
  val storeUnits = Seq.fill(numStoreUnits)(Module(new StoreUnit).io)
  
  val accumStage = Wire(new AccumStage)
  accumStage.result := 0.U

  // Initialize Data memory interface signals
  io.dmem.address := 0.U(32.W)
  io.dmem.writeData := 0.U(32.W)
  io.dmem.writeEnable := false.B
  val memStages = Seq.fill(numLoadUnits + numStoreUnits)(Wire(new MemStage))
  memStages.foreach { m =>
    m.result := 0.U
    m.valid := false.B
    m.writeEnable := false.B
}

  // Clock gating logic 
  val enableALU = Wire(Bool())
  val enableMul = Wire(Bool())
  val enableVec = Wire(Bool())

  enableALU := true.B
  enableMul := false.B
  enableVec := false.B

  // Fetch stage: Load 8 instructions per cycle into the instruction window
  for (i <- 0 until 8) {
    instructionWindow(i) := io.imem.readData(((pc(currentThread) + (i.U * 4.U))(31,0)) & "hFFFFFFFC".U(32.W))
  }
  pc(currentThread) := pc(currentThread) + 32.U // Advance PC by 8 instructions

  // Decode and issue logic
  val decodePtr = RegInit(0.U(log2Ceil(instructionWindowSize).W))
  decodeStage.instruction := instructionWindow(decodePtr)
  decodeStage.pc := pc(currentThread) + (decodePtr * 4.U)

  when(decodeStage.instruction =/= 0.U) {
    issueBuffer(currentThread)(decodePtr) := true.B // Mark instruction as ready to issue
    decodePtr := decodePtr + 1.U
  }

  // Issue buffer removes structural hazards and checks for data dependencies
  val issuedInstruction = Wire(UInt(32.W))
  val issuedThread = Wire(UInt(log2Ceil(numThreads).W))

  issuedInstruction := 0.U
  issuedThread := 0.U

  for (t <- 0 until numThreads) {
    for (i <- 0 until instructionWindowSize) {
      val currentInstruction = instructionWindow(i)
      val rs1 = currentInstruction(19, 15) // Extract source register 1
      val rs2 = currentInstruction(24, 20) // Extract source register 2
      val rd = currentInstruction(11, 7)  // Extract destination register

      val dependencyFound = (0 until instructionWindowSize).map { j =>
        val issuedInstruction = instructionWindow(j)
        val issuedRd = issuedInstruction(11, 7)
        issueBuffer(t)(j) && ((rs1 === issuedRd) || (rs2 === issuedRd))
      }.reduce(_ || _)

      when(issueBuffer(t)(i) && !dependencyFound) {
        issuedInstruction := currentInstruction
        issuedThread := t.U
        issueBuffer(t)(i) := false.B // Remove issued instruction

        // Enable clock gating for units based on opcode
        when(currentInstruction(6, 0) === "b0110011".U) { // ALU operation
          enableALU := true.B
        } .elsewhen(currentInstruction(6, 0) === "b0000011".U) { // Load operation
          enableMul := true.B
        } .elsewhen(currentInstruction(6, 0) === "b1010111".U) { // Vector operation
          enableVec := true.B
        }
      }
    }
  }

  decodeStage.instruction := issuedInstruction

  // Connect ALU execution stages
  aluExecStages.foreach { alu =>
    alu.op1 := decodeStage.rs1
    alu.op2 := decodeStage.rs2
  }

  // Connect Multiply execution stages
  mulExecStages.foreach { mul =>
    mul.op1 := decodeStage.rs1
    mul.op2 := decodeStage.rs2
  }

  // Connect Vector execution stages
  vecExecStages.foreach { vec =>
    vec.op1 := decodeStage.rs1
    vec.op2 := decodeStage.rs2
  }

  // Connect Load Units
  loadUnits.foreach { load =>
    load.address := decodeStage.rs1
  }

  // Connect Store Units
  storeUnits.foreach { store =>
    store.address := decodeStage.rs1
    store.data := decodeStage.rs2
  }

  // Memory stages (Load/Store)
  memStages.zipWithIndex.foreach { case (memStage, idx) =>
    memStage.address := MuxCase(0.U, Seq(
      (decodeStage.op === 0.U) -> aluExecStages.head.result,
      (decodeStage.op === 1.U) -> mulExecStages.head.result,
      (decodeStage.op === 2.U) -> vecExecStages.head.result,
      (decodeStage.op === 3.U) -> accumStage.result
    ))
    memStage.data := io.dmem.readData(memStage.address(31,0)  & "hFFFFFFFF".U(32.W))
  }

  // Writeback
  when(memStages.head.valid) {
    memStages.head.result := io.dmem.readData(memStages.head.address(31,0)  & "hFFFFFFFF".U(32.W))
  }

  // Output connections
  io.imem.address := pc(currentThread)
  io.imem.writeData := 0.U
  io.imem.writeEnable := 0.U

  io.dmem.address := memStages.head.address
  io.dmem.writeEnable := memStages.head.writeEnable
  io.dmem.writeData := 0.U
}

// Stubbed Execution Units
class ALU extends Module {
  val io = IO(new Bundle {
    val op1 = Input(UInt(32.W))
    val op2 = Input(UInt(32.W))
    val result = Output(UInt(32.W))
  })
  io.result := io.op1 + io.op2 // Stubbed ALU operation
}

class Multiplier extends Module {
  val io = IO(new Bundle {
    val op1 = Input(UInt(32.W))
    val op2 = Input(UInt(32.W))
    val result = Output(UInt(32.W))
  })
  io.result := io.op1 * io.op2 // Stubbed Multiply operation
}

class VectorUnit extends Module {
  val io = IO(new Bundle {
    val op1 = Input(UInt(32.W))
    val op2 = Input(UInt(32.W))
    val result = Output(UInt(32.W))
  })
  io.result := io.op1 << io.op2(4,0)  // Ensure shift amount is at most 31 bits
}

class LoadUnit extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(32.W))
    val data = Output(UInt(32.W))
  })
  io.data := io.address // Stubbed Load operation
}

class StoreUnit extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(32.W))
    val data = Input(UInt(32.W))
  })
  // Stubbed Store operation - no effect
}

// Define pipeline stages
class DecodeStage extends Bundle {
  val pc = UInt(32.W)
  val instruction = UInt(32.W)
  val rs1 = UInt(32.W)
  val rs2 = UInt(32.W)
  val op = UInt(8.W)
}

class AccumStage extends Bundle {
  val result = UInt(32.W)
}

class MemStage extends Bundle {
  val address = UInt(32.W)
  val data = UInt(32.W)
  val result = UInt(32.W)
  val valid = Bool()
  val writeEnable = Bool()
}

// Memory interface
class MemoryInterface extends Bundle {
  val address = Output(UInt(32.W))  // Core always drives address
  val writeEnable = Output(Bool())  // Core sets writeEnable to indicate a write
  val writeData = Output(UInt(32.W)) // Core drives data when writing
  val readData = Input(UInt(32.W))   // Memory provides data when reading
}
