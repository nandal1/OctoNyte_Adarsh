// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package TetraNyte

import chisel3._
import chisel3.util._

/**
  * A multithreaded 2-read, 1-write register file implemented as a vector of registers.
  *
  * The register file contains `depth` registers per thread (default 32),
  * and there are `numThreads` independent register sets.
  *
  * The effective address is computed by concatenating the threadID (as the high bits)
  * with the register index (as the low bits). For example, if depth = 32 (5 bits)
  * and numThreads = 4 (2 bits), then the effective address width is 7 bits,
  * and there are 128 registers in total.
  *
  * IO:
  *   - threadID: Input specifying the current thread (width = log2Ceil(numThreads)).
  *   - src1, src2: Read port register indices (each width = log2Ceil(depth)).
  *   - dst1: Write port register index (width = log2Ceil(depth)).
  *   - wen: Write enable.
  *   - dst1data: Data to be written.
  *   - src1data, src2data: Outputs from read ports.
  */
class RegFileMT2R1WVec(width: Int = 32, depth: Int = 32, numThreads: Int = 4) extends Module {
  // Calculate the number of bits needed for a register index (per thread)
  val addrWidth    = log2Ceil(depth)
  // Calculate bits required to represent the thread ID.
  val threadWidth  = log2Ceil(numThreads)
  // The effective depth is the total number of registers across all threads.
  val effectiveDepth = depth * numThreads
  // The effective address width is log2Ceil(effectiveDepth)
  val effectiveAddrWidth = log2Ceil(effectiveDepth)

  val io = IO(new Bundle {
    val threadID  = Input(UInt(threadWidth.W))   // Thread identifier
    val src1      = Input(UInt(addrWidth.W))     // Read port 1 address (per-thread)
    val src2      = Input(UInt(addrWidth.W))     // Read port 2 address (per-thread)
    val dst1      = Input(UInt(addrWidth.W))     // Write port address (per-thread)
    val wen       = Input(Bool())                // Write enable
    val dst1data  = Input(UInt(width.W))         // Data to write
    val src1data  = Output(UInt(width.W))        // Read port 1 data
    val src2data  = Output(UInt(width.W))        // Read port 2 data
  })

  // Create the register file as a vector of registers.
  // Total number of registers = depth * numThreads.
  val regs = RegInit(VecInit(Seq.fill(effectiveDepth)(0.U(width.W))))

  // Compute effective addresses by concatenating the thread ID (MSBs) with the register index.
  val effectiveSrc1 = Cat(io.threadID, io.src1)  // Result is UInt(effectiveAddrWidth.W)
  val effectiveSrc2 = Cat(io.threadID, io.src2)
  val effectiveDst1 = Cat(io.threadID, io.dst1)

  // Write logic: write to the register file if write enable is high.
  when(io.wen) {
    regs(effectiveDst1) := io.dst1data
  }

  // Read logic: the outputs are generated combinationally.
  io.src1data := regs(effectiveSrc1)
  io.src2data := regs(effectiveSrc2)
}
