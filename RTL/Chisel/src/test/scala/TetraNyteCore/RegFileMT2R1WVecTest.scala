// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.
//
// sbt "testOnly TetraNyte.RegFileMT2R1WVecTest"

package TetraNyte

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Unit test for the multithreaded 2-read/1-write register file (RegFileMT2R1WVec).
  *
  * This test writes a value to a specific register (dst) for several different threads,
  * then reads back from that register via both read ports to verify that each thread’s register
  * bank is independent.
  */
class RegFileMT2R1WVecTest extends AnyFlatSpec {
  "RegFileMT2R1WVec" should "correctly perform independent read/write accesses per thread" in {
    // Instantiate the register file with width = 32, depth = 32 per thread, and 4 threads.
    simulate(new RegFileMT2R1WVec(width = 32, depth = 32, numThreads = 4)) { dut =>
      
      // Helper function that writes a value to a register for a given thread,
      // then reads back the value using both read ports.
      def writeAndRead(
        thread: BigInt,
        regIdx: BigInt,
        writeData: BigInt
      ): Unit = {
        // Set the current thread for the write
        dut.io.threadID.poke(thread.U)
        // Set the destination register index and data, and enable the write.
        dut.io.dst1.poke(regIdx.U)
        dut.io.dst1data.poke(writeData.U)
        dut.io.wen.poke(true.B)
        dut.clock.step(1) // Latch the write on the clock edge.
        // Disable write for subsequent operations.
        dut.io.wen.poke(false.B)
        
        // Now set the read addresses to the same register.
        dut.io.src1.poke(regIdx.U)
        dut.io.src2.poke(regIdx.U)
        
        // Because the read ports are combinational, we can peek immediately.
        val read1 = dut.io.src1data.peek().litValue
        val read2 = dut.io.src2data.peek().litValue
        
        assert(read1 == writeData, s"Thread $thread, register $regIdx (read port1): expected $writeData, got $read1")
        assert(read2 == writeData, s"Thread $thread, register $regIdx (read port2): expected $writeData, got $read2")
      }
      
      // Test writing and reading for each thread.
      // For thread 0, write to register 5, value 123.
      writeAndRead(0, 5, 123)
      
      // For thread 1, write to register 5, value 456.
      writeAndRead(1, 5, 456)
      
      // For thread 2, write to register 10, value 789.
      writeAndRead(2, 10, 789)
      
      // For thread 3, write to register 7, value 101112.
      writeAndRead(3, 7, 101112)
      
      // Additionally, verify that registers that were not written in a given thread are still 0.
      dut.io.threadID.poke(0.U)
      dut.io.src1.poke(8.U)
      dut.io.src2.poke(8.U)
      val zeroVal1 = dut.io.src1data.peek().litValue
      val zeroVal2 = dut.io.src2data.peek().litValue
      assert(zeroVal1 == 0, s"Thread 0, register 8 expected 0, got $zeroVal1")
      assert(zeroVal2 == 0, s"Thread 0, register 8 expected 0, got $zeroVal2")
    }
  }
}
