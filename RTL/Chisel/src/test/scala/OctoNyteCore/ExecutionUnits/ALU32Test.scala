// Licensed under the BSD 3-Clause License.
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.ALUs

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.flatspec.AnyFlatSpec
import scabook.ALUs.ALU32.Opcode

class ALU32Test extends AnyFlatSpec {

  "ALU32" should "correctly compute addition subtraction logical and shift for all opcodes and test Flags" in {
    simulate(new ALU32) { dut =>
      val printDebugInfo = false

      testArithmetic()
      testLogic()

      def testOperation(a: BigInt, b: BigInt, opcode: UInt, expected: BigInt, 
                        expectedFlag: Boolean, expectedZeroFlag: Boolean, expectedNegativeFlag: Boolean): Unit = {
        val isSigned = opcode(2).litToBoolean

        if (isSigned) {
          dut.io.a.poke(a.S(32.W).asUInt)
          dut.io.b.poke(b.S(32.W).asUInt)
        } else {
          dut.io.a.poke(a.U(32.W))
          dut.io.b.poke(b.U(32.W))
        }
        dut.io.opcode.poke(opcode)
        dut.clock.step()
        val result = dut.io.result.peek().litValue
        val carryOut = dut.io.carryOutFlag.peek().litValue == 1
        val overflowFlag = dut.io.overflowFlag.peek().litValue == 1
        val zeroFlag = dut.io.zeroFlag.peek().litValue == 1
        val negativeFlag = dut.io.negativeFlag.peek().litValue == 1
        if(printDebugInfo) println(s"[ALU32 - Result] -- Sum: $result, Carry: $carryOut, zeroFlag: $zeroFlag, negativeFlag $negativeFlag")
        
        assert(result == expected, s"[ALU32] -- Expected result 0x${expected.toString(16)} but got 0x${result.toString(16)} for opcode $opcode")
        if( isSigned ) {
          assert(overflowFlag == expectedFlag, s"[ALU32] -- Expected Overflow Flag $expectedFlag but got overflowFlag $overflowFlag for opcode= $opcode and isSigned= $isSigned")
        } else {
          assert(carryOut == expectedFlag, s"[ALU32] -- ExpectedFlag $expectedFlag but got carryOut $carryOut for opcode $opcode")
        }
        assert(zeroFlag == expectedZeroFlag, s"[ALU32] -- ExpectedZeroFlag $expectedZeroFlag but got zeroFlag $zeroFlag for opcode $opcode")
      }

      def testArithmetic(): Unit = {
        val opcodes = Seq(
          (Opcode.ADD_U8, false, 8), (Opcode.ADD_U16, false, 16),
          (Opcode.ADD_U32, false, 32), (Opcode.SUB_U8, false, 8),
          (Opcode.SUB_U16, false, 16), (Opcode.SUB_U32, false, 32),
          (Opcode.ADD_S8, true, 8), (Opcode.ADD_S16, true, 16),
          (Opcode.ADD_S32, true, 32), (Opcode.SUB_S8, true, 8),
          (Opcode.SUB_S16, true, 16), (Opcode.SUB_S32, true, 32)
        )

        for ((opcode, signed, width) <- opcodes) {
          val isSub = opcode(3).litToBoolean        
          val isAdd = !isSub
          val expected = if (isSub) (4 - 3)  else (4 + 3)
          if (isAdd) testOperation(4, 3, opcode, expected, false, false, false)
          if (isSub) testOperation(4, 3, opcode, expected, false, false, false)
        }
      }
      
      def testLogic(): Unit = {
        val opcodes = Seq(
          ALU32.Opcode.AND_U8, ALU32.Opcode.AND_U16, ALU32.Opcode.AND_U32,
          ALU32.Opcode.OR_U8, ALU32.Opcode.OR_U16, ALU32.Opcode.OR_U32,
          ALU32.Opcode.XOR_U8, ALU32.Opcode.XOR_U16, ALU32.Opcode.XOR_U32,
          ALU32.Opcode.SLL_U8, ALU32.Opcode.SLL_U16, ALU32.Opcode.SLL_U32,
          ALU32.Opcode.SRL_U8, ALU32.Opcode.SRL_U16, ALU32.Opcode.SRL_U32,
          ALU32.Opcode.SRA_U8, ALU32.Opcode.SRA_U16, ALU32.Opcode.SRA_U32
          //Test Adding repository to SP25 branch
        )

        for (opcode <- opcodes) {
          val operandSize = (opcode.litValue & 0x3).toInt match {
            case 0 => 8
            case 1 => 16
            case 2 => 32
          }
          val mask = (BigInt(1) << operandSize) - 1
          val a = BigInt("F" * (operandSize / 4), 16)
          val b = BigInt("A" * (operandSize / 4), 16)
          val expectedResult: BigInt = opcode.litValue.toInt match {
            case v if v >= ALU32.Opcode.AND_U8.litValue.toInt && v <= ALU32.Opcode.AND_U32.litValue.toInt =>
              (a & b) & mask
            case v if v >= ALU32.Opcode.OR_U8.litValue.toInt && v <= ALU32.Opcode.OR_U32.litValue.toInt =>
              (a | b) & mask
            case v if v >= ALU32.Opcode.XOR_U8.litValue.toInt && v <= ALU32.Opcode.XOR_U32.litValue.toInt =>
              (a ^ b) & mask
            case v if v >= ALU32.Opcode.SLL_U8.litValue.toInt && v <= ALU32.Opcode.SLL_U32.litValue.toInt =>
              ((a << (b.toInt & (operandSize - 1))) & mask)
            case v if v >= ALU32.Opcode.SRL_U8.litValue.toInt && v <= ALU32.Opcode.SRL_U32.litValue.toInt =>
              ((a >> (b.toInt & (operandSize - 1))) & mask)
            case v if v >= ALU32.Opcode.SRA_U8.litValue.toInt && v <= ALU32.Opcode.SRA_U32.litValue.toInt =>
              ((BigInt(a.toLong) >> (b.toInt & (operandSize - 1))) & mask)
          }
          testOperation(a, b, opcode, expectedResult, false, false, false)
        }
      }
    }
  }
}
