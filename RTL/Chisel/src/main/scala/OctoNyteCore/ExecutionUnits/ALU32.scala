// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.

package scabook.ALUs

import chisel3._
import chisel3.util._

import OctoNyte.ExecutionUnits.RISCVAdderSubtractor32


////Test Adding repository to SP25 branch
// Opcodes
// b5: arithmetic/logic
// b1b0: 8/16/32/64 bits
object ALU32 {
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

class ALU32 extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(32.W))
    val b = Input(UInt(32.W))
    val result = Output(UInt(32.W))
    val opcode = Input(UInt(6.W)) 
    val carryOutFlag = Output(UInt(1.W))  //Carry and Borrow
    val overflowFlag = Output(UInt(1.W))  //V
    val zeroFlag = Output(UInt(1.W))      //Z
    val negativeFlag = Output(UInt(1.W))  //N
  })

  val printDebugInfo = false

  // Debug internals of ALU
  if(printDebugInfo) printf(p"[Inside ALU32] --\n") 

  // Decode control signals
  // b5: arithetic / logic
  // b4b3b2: logic ope
  // b3: if arith: Add/Sub
  // b2: if arith: Signed/Unsigned
  // b1b0: 8/16/32/64 bits
  // Decode control signals
  val isArithmetic = io.opcode(5) === 0.U // MSB: 0 for arithmetic
  val isLogical = io.opcode(5) === 1.U // MSB: 1 for logical operations
  val isSub = io.opcode(3) === 1.U && isArithmetic // b3: 1 for subtraction
  val isSigned = io.opcode(2) && isArithmetic // b2: 1 for signed
  val operandSize = io.opcode(1, 0) // b1b0: 00 for 8-bit, 01 for 16-bit, 10 for 32-bit, 11 for 64-bit

  // Determine effective width based on operand size
  val width = WireDefault(32.U)
  val mask = WireDefault("hffffffff".U)
  switch(operandSize) {
    is("b00".U) { width := 8.U;  mask := "h000000ff".U }
    is("b01".U) { width := 16.U; mask := "h0000ffff".U }
    is("b10".U) { width := 32.U; mask := "hffffffff".U }
   // is("b11".U) { width := 64.U; mask := "hffffffff".U }
  }

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

  //***********************
  // Arithmetic operations
  //***********************
  val adderSubtractor = Module(new RISCVAdderSubtractor32)

  // Connect inputs
  adderSubtractor.io.a := aEffective
  adderSubtractor.io.b := bEffective
  adderSubtractor.io.carryIn := 0.U(1.W)
  adderSubtractor.io.opcode := io.opcode(3, 0)

  // Connect outputs to the ALU64 outputs
  io.result := adderSubtractor.io.result
  io.carryOutFlag := adderSubtractor.io.carryOut
  io.overflowFlag := adderSubtractor.io.overflowFlag
  io.zeroFlag := adderSubtractor.io.zeroFlag
  io.negativeFlag := adderSubtractor.io.negativeFlag
  

  //********************
  // Logical operations
  //********************
  // b4b3b2: logical operation
  val logicalResult = MuxCase(0.U(32.W), Seq(
    (io.opcode(4, 2) === "b000".U) -> (aEffective & bEffective),                               // AND
    (io.opcode(4, 2) === "b001".U) -> (aEffective | bEffective),                               // OR
    (io.opcode(4, 2) === "b010".U) -> (aEffective ^ bEffective),                               // XOR
    (io.opcode(4, 2) === "b011".U) -> ((aEffective << (bEffective(5, 0) & (width - 1.U))).asUInt & mask), // SLL
    (io.opcode(4, 2) === "b100".U) -> ((aEffective >> (bEffective(5, 0) & (width - 1.U))).asUInt & mask), // SRL
    (io.opcode(4, 2) === "b101".U) -> ((aEffective.asSInt >> (bEffective(5, 0) & (width - 1.U))).asUInt & mask) // SRA
  ))

  if(printDebugInfo) printf(p"  logicalResult =     0x${Hexadecimal(logicalResult)}\n")

  //**************************
  // Assign results to outputs
  //**************************
  io.result := Mux(isArithmetic, adderSubtractor.io.result, logicalResult)
  io.carryOutFlag := Mux(isArithmetic, adderSubtractor.io.carryOut, 0.U)
  io.overflowFlag := Mux(isArithmetic, adderSubtractor.io.overflowFlag, 0.U)
  io.zeroFlag := Mux(isArithmetic, adderSubtractor.io.zeroFlag, 0.U)
  io.negativeFlag := Mux(isArithmetic, adderSubtractor.io.negativeFlag, 0.U)

}