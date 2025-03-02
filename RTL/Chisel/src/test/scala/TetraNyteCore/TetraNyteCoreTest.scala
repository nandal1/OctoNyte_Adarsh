// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.
//
// sbt 'testOnly TetraNyte.TetraNyteCoreTest'

package TetraNyte

import chisel3._
import chisel3.util._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import TetraNyte.TetraNyteCore

class TetraNyteCoreTest extends AnyFlatSpec {

  "TetraNyteCore" should "fill the 4-stage pipeline and run for a few cycles" in {
    simulate(new TetraNyteCore) { dut =>
      
      // Preload instruction memory with NOPs (encoded as 0x00000013)
      // We write into the instruction memory using the I/O signals.
      dut.io.debug.poke(false.B)
      for (addr <- 0 until 16) {
        dut.io.instrWriteAddr.poke(addr.U)
        dut.io.instrWriteData.poke(0x00000013.U(32.W))
        dut.io.instrWriteEnable.poke(true.B)
        dut.clock.step(1)  // perform the write
        dut.io.instrWriteEnable.poke(false.B)
        dut.clock.step(1)
      }
      
      // Now run the core for a number of cycles.
      // The debug prints inside TetraNyteCore should show the pipeline state.
      dut.io.debug.poke(true.B)
      for (cycle <- 0 until 12) {
        println(s"--- Cycle $cycle ---")
        dut.clock.step(1)
        // Example: Peek at the memory address output (in hex)
        val memAddr = dut.io.memAddr.peek().litValue
        println(s"Memory Address: 0x${memAddr.toString(16)}")
      }
    }
  }
}
