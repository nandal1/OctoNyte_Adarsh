// Licensed under the BSD 3-Clause License. 
// See https://opensource.org/licenses/BSD-3-Clause for details.


import chisel3._
import chisel3.util._
import OctoNyteCore.OctoNyteCore

class OctoNyteTestHarness extends Module {
  val io = IO(new Bundle {
    val success = Output(Bool()) // Indicates whether the test passed
  })

  // Instantiate core
  val core = Module(new OctoNyteCore())
  
  core.io.interrupt := false.B
  core.io.interruptMask.foreach(_ := false.B)


  // Define SimpleMemory inside TestHarness
  class SimpleMemory(val depth: Int = 1024) extends Module {
    val io = IO(new Bundle {
      val address = Input(UInt(32.W))
      val writeEnable = Input(Bool())
      val writeData = Input(UInt(32.W))
      val readData = Output(UInt(32.W))
    })

    val mem = SyncReadMem(depth, UInt(32.W))

    io.readData := mem.read(io.address, !io.writeEnable)
    when(io.writeEnable) {
      mem.write(io.address, io.writeData)
    }
  }

  // Instantiate Instruction memory
  val imem = Module(new SimpleMemory(depth = 1024))
  // Connect memory interface properly
  imem.io.address := core.io.imem.address
  imem.io.writeEnable := core.io.imem.writeEnable
  imem.io.writeData := core.io.imem.writeData
  core.io.imem.readData := imem.io.readData
  io.success := imem.io.readData === 0.U

  // Instantiate Data memory
  val dmem = Module(new SimpleMemory(depth = 1024))
  // Connect memory interface properly
  dmem.io.address := core.io.dmem.address
  dmem.io.writeEnable := core.io.dmem.writeEnable
  dmem.io.writeData := core.io.dmem.writeData
  core.io.dmem.readData := dmem.io.readData
  //io.success := imem.io.readData === 0.U

}
