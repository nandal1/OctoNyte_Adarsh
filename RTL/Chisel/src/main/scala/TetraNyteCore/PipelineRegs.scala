package TetraNyte

import chisel3._
import chisel3.util._

/** A single, "kitchen-sink" pipeline register bundle with every field. */
class PipelineRegBundle extends Bundle {
  val threadId    = UInt(2.W)  // For 4 threads
  val valid       = Bool()

  // Basic instruction fields
  val pc          = UInt(32.W)
  val instr       = UInt(32.W)

  // Decoded signals
  val isALU       = Bool()
  val isLoad      = Bool()
  val isStore     = Bool()
  val isBranch    = Bool()
  val isJAL       = Bool()
  val isJALR      = Bool()
  val isLUI       = Bool()
  val isAUIPC     = Bool()
  val isFence     = Bool()
  val isSystem    = Bool()
  val aluOp       = UInt(5.W)

  // Immediate
  val imm         = UInt(32.W)

  // Register file signals
  val rs1         = UInt(5.W)
  val rs2         = UInt(5.W)
  val rd          = UInt(5.W)
  val rs1Data     = UInt(32.W)
  val rs2Data     = UInt(32.W)

  // Memory signals
  val memAddr     = UInt(32.W)
  val memWdata    = UInt(32.W)
  val memRdata    = UInt(32.W)

  // ALU result placeholder
  val aluResult   = UInt(32.W)
}

/** A helper function to copy pipeline-register data for `currentThread`
  * from stage `fromVec` to stage `toVec`.
  */
object PipelineHelpers {
  def updatePipelineStage(
    currentThread: UInt,
    fromVec: Vec[PipelineRegBundle],
    toVec:   Vec[PipelineRegBundle]
  ): Unit = {
    toVec(currentThread) := fromVec(currentThread)
  }
}
