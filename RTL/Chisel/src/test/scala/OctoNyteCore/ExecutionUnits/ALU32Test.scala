// Licensed under the BSD 3-Clause License.
// See https://opensource.org/licenses/BSD-3-Clause for details.

package OctoNyte.ExecutionUnits

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import OctoNyte.ExecutionUnits.ALU32.Opcode

class ALU32Test extends AnyFlatSpec {

  "ALU32" should "correctly compute arithmetic, logical, and shift operations for all opcodes and test flags" in {

    simulate(new ALU32) { dut =>
      val printDebugInfo = false

      testArithmetic()
      testLogic()

      def testOperation(a: BigInt, b: BigInt, opcode: UInt, expected: BigInt, 
                        expectedFlag: Boolean, expectedZeroFlag: Boolean, expectedNegativeFlag: Boolean): Unit = {

        dut.io.a.poke(a.U)
        dut.io.b.poke(b.U)
        dut.io.opcode.poke(opcode)
        dut.clock.step()


        val result = dut.io.result.peek().litValue
        val carryOut = dut.io.carryOutFlag.peek().litValue == 1
        val overflowFlag = dut.io.overflowFlag.peek().litValue == 1
        val zeroFlag = dut.io.zeroFlag.peek().litValue == 1
        val negativeFlag = dut.io.negativeFlag.peek().litValue == 1

        if (printDebugInfo) println(s"[ALU32 - Result] -- Result: $result, Carry: $carryOut, Zero: $zeroFlag, Negative: $negativeFlag")

        assert(result == expected, s"[ALU32] -- Expected result 0x${expected.toString(16)} but got 0x${result.toString(16)} for opcode $opcode")
        assert(carryOut == expectedFlag, s"[ALU32] -- Expected carry flag $expectedFlag but got $carryOut for opcode $opcode")
        assert(zeroFlag == expectedZeroFlag, s"[ALU32] -- Expected zero flag $expectedZeroFlag but got $zeroFlag for opcode $opcode")

      }

      def testArithmetic(): Unit = {
        val opcodes = Seq(

          Opcode.ADD, Opcode.SUB, Opcode.ADDI, Opcode.SLTI
        )

        for (opcode <- opcodes) {
          val isSub = opcode === Opcode.SUB
          val expected = if (isSub.litToBoolean) 4 - 3 else 4 + 3
          testOperation(4, 3, opcode, expected, false, expected == 0, expected < 0)
        }
      }

      def testLogic(): Unit = {
        val opcodes = Seq(
          Opcode.AND, Opcode.OR, Opcode.XOR, Opcode.SLL, Opcode.SRL, Opcode.SRA
        )

        for (opcode <- opcodes) {
          val a = 0xF0F0F0F0
          val b = 0x0F0F0F0F
          val expected = opcode.litValue.toInt match {
            case x if x == Opcode.AND.litValue.toInt => a & b
            case x if x == Opcode.OR.litValue.toInt  => a | b
            case x if x == Opcode.XOR.litValue.toInt => a ^ b
            case x if x == Opcode.SLL.litValue.toInt => a << (b & 0x1F)
            case x if x == Opcode.SRL.litValue.toInt => a >>> (b & 0x1F)
            case x if x == Opcode.SRA.litValue.toInt => (a.toInt >> (b & 0x1F)) & 0xFFFFFFFFL
          }
          testOperation(a, b, opcode, expected, false, expected == 0, expected < 0)

        }
      }
    }
  }
}
