// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.
//
// sbt 'testOnly TetraNyte.TetraNyteCoreTest'

package TetraNyte

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import TetraNyte.TetraNyteCore

class TetraNyteCoreTest extends AnyFlatSpec {

  "TetraNyteCore" should "fill the pipeline by around cycle 6 and run for a few steps" in {
    simulate(new TetraNyteCore) { dut =>

      for (addr <- 0 until 16) {
        // Write NOP (0x13) into instrMem at address 'addr'
        dut.io.instrWriteAddr.poke(addr.U)
        dut.io.instrWriteData.poke(0x00000013.U)
        dut.io.instrWriteEnable.poke(true.B)
        dut.clock.step(1)  // advance one cycle so the write occurs

        // Turn off writeEnable
        dut.io.instrWriteEnable.poke(false.B)
        dut.clock.step(1)
 
        
      }
    }
  }
}
