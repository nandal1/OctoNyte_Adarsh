// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

// sbt "testOnly OctoNyteTestSpec"

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec

class OctoNyteTestSpec extends AnyFlatSpec {
  "OctoNyte Core" should "execute a simple load-store test" in {
    simulate(new OctoNyteTestHarness) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step(1)
      dut.reset.poke(false.B)
      
      // Run for enough cycles to complete execution
      dut.clock.step(20)
      
      // Check success flag
      assert(dut.io.success.peek().litToBoolean, "Test failed!")
    }
  }
}
