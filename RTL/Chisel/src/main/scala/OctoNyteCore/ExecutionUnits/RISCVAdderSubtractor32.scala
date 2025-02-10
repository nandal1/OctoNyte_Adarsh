// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package OctoNyte.ExecutionUnits

import chisel3._
import chisel3.util._


// Opcodes
// b3: Add/Sub
// b2: Signed/Unsigned
// b1b0: 8/16/32/64 bits
object RISCVAdderSubtractor32 {

  object Opcode {
    object Opcode {
    val ADD  = "b00000".U
    val SUB  = "b00001".U
    val XOR  = "b01000".U
    val OR   = "b01100".U
    val AND  = "b01110".U
    val SLL  = "b00010".U
    val SRL  = "b01010".U
    val SRA  = "b01011".U
    val SLT  = "b00100".U
    val SLTU = "b00110".U

    // Immediate Type
    val ADDI  = "b10000".U
    val XORI  = "b11000".U
    val ORI   = "b11100".U
    val ANDI  = "b11110".U
    val SLLI  = "b10010".U
    val SRAL  = "b11010".U
    val SRAI  = "b11011".U
    val SLTI  = "b10100".U
    val SLTUI = "b10110".U
  }
}

  // Companion object 
  def apply(
    a: UInt,
    b: UInt,
    opcode: UInt,
    carryIn: Option[UInt] = None // optional
  ): RISCVAdderSubtractor32 = {
    val module = Module(new RISCVAdderSubtractor32)
    module.io.a := a
    module.io.b := b
    module.io.opcode := opcode
    carryIn match {
      case Some(value) => module.io.carryIn := value // Connect when provided
      case None => module.io.carryIn := 0.U         // Default to 0 if not provided
    }
    module
  }
}

class RISCVAdderSubtractor32 extends RISCVAdderSubtractor(32) {

  val printDebugInfo = false

  // Decode control signals
  // b3: Add/Sub
  // b2: Signed/Unsigned
  // b1b0: 8/16/32/64 bits
  val isSub = io.opcode(3) === 1.U // b3: 1 for subtraction
  val isSigned = io.opcode(2)      // b2: 1 for signed
  val operandSize = io.opcode(1, 0) // b1b0: 00 for 8-bit, 01 for 16-bit, 10 for 32-bit, 11 for 64-bit

  // Determine effective width based on operand size
  val width = MuxCase(32.U, Seq(
    (operandSize === "b00".U) -> 8.U,
    (operandSize === "b01".U) -> 16.U,
    (operandSize === "b10".U) -> 32.U,
    //(operandSize === "b11".U) -> 64.U
  ))

  val mask = MuxCase("hffffffff".U, Seq(
    (operandSize === "b00".U) -> "h000000ff".U,
    (operandSize === "b01".U) -> "h0000ffff".U,
    (operandSize === "b10".U) -> "hffffffff".U,
    //(operandSize === "b11".U) -> "hffffffffffffffff".U
  ))

  // Mask inputs to the appropriate width
  val aEffective = io.a & mask
  val bEffective = io.b & mask
  val bAdjusted = Mux(isSub, (~bEffective + 1.U), bEffective)

  if(printDebugInfo){
    printf(p"  width = ${width}, isSigned = ${isSigned}, isSub = ${isSub}\n")
    printf(p"  mask =       0x${Hexadecimal(mask)}\n")
    printf(p"  a =          0x${Hexadecimal(io.a)}\n")
    printf(p"  b =          0x${Hexadecimal(io.b)}\n")
    printf(p"  aEffective = 0x${Hexadecimal(aEffective)},\n")
    printf(p"  bEffective = 0x${Hexadecimal(bEffective)},\n")
    printf(p"  bAdjusted =  0x${Hexadecimal(bAdjusted)},\n")
  }

  //**********************
  // Arithmetic operations
  //***********************
  val fullArithmeticResult = Mux(isSigned,
    (aEffective.asSInt +& bAdjusted.asSInt).asUInt, // Signed operation
    (aEffective +& bAdjusted)                       // Unsigned operation
  )
  val truncatedResult = fullArithmeticResult & mask
  
  if(printDebugInfo) {
    printf(p"  fullArithmeticResult = 0x${Hexadecimal(fullArithmeticResult)}\n")
    printf(p"  truncatedResult =     0x${Hexadecimal(truncatedResult)}\n")
  }
 
  //*************
  // Final result
  //*************
  io.result := truncatedResult
  if(printDebugInfo) printf(p"  io.result =     0x${Hexadecimal(io.result)}\n")

  //**************
  // Compute Flags
  //**************

  // Carry Flag: Unsigned Arithmetic

  // Chisel considers width a dynamic construct
  //   Unfortunately, every width must be explicit

  val isCarry = MuxCase(false.B, Seq(
    (width === 8.U)  -> fullArithmeticResult(8),
    (width === 16.U) -> fullArithmeticResult(16),
    (width === 32.U) -> fullArithmeticResult(32),
    //(width === 64.U) -> fullArithmeticResult(64)
  ))


 // Borrow
  val isBorrow = Mux(isSub && !isSigned, aEffective < bEffective, false.B)

  if(printDebugInfo) printf(p"  isCarry =     0x${Hexadecimal(isCarry)}\n")
  if(printDebugInfo) printf(p"  isBorrow =    0x${Hexadecimal(isBorrow)}\n")

  io.carryOut := MuxCase(0.U, Seq(
    (!isSigned && !isSub) -> isCarry, 
    (!isSigned &&  isSub) -> isBorrow
  )) 

  if(printDebugInfo) printf(p"  io.carryOut =     0x${Hexadecimal(io.carryOut)}\n")

  // Overflow: Signed Arithmetic
  // Adding with different signs can't cause overflow
  // Overflow = both operand signs the same, result sign different
  val aSign = WireDefault(false.B)
  val bSign = WireDefault(false.B)
  val sumSign = WireDefault(false.B)

  aSign := MuxCase(false.B, Seq(
    (width === 8.U)  -> aEffective(7),
    (width === 16.U) -> aEffective(15),
    (width === 32.U) -> aEffective(31),
    //(width === 64.U) -> aEffective(63)
  ))

  bSign := MuxCase(false.B, Seq(
    (width === 8.U)  -> bAdjusted(7),
    (width === 16.U) -> bAdjusted(15),
    (width === 32.U) -> bAdjusted(31),
    //(width === 64.U) -> bAdjusted(63)
  ))

  sumSign := MuxCase(false.B, Seq(
    (width === 8.U)  -> fullArithmeticResult(7),
    (width === 16.U) -> fullArithmeticResult(15),
    (width === 32.U) -> fullArithmeticResult(31),
    //(width === 64.U) -> fullArithmeticResult(63)
  ))

  val isOverflow = (aSign === bSign) && (aSign =/= sumSign)
  if (printDebugInfo) printf(p"  overflowFlag =     0x${Hexadecimal(isOverflow)}\n")
  io.overflowFlag := isOverflow

  val isZero = !truncatedResult.orR // Reduction OR to check if any bit in `io.result` is 1
  if (printDebugInfo) printf(p"  zeroFlag =     0x${Hexadecimal(isZero)}\n")
  io.zeroFlag := isZero

  val isNegative = Mux(isSigned, sumSign, false.B)
  io.negativeFlag := isNegative
}