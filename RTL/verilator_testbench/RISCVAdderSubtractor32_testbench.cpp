// File: testbench.cpp
#include "VRISCVAdderSubtractor32.h"  // Verilator-generated header
#include "verilated.h"
#include <iostream>
#include <cassert>
#include <cstdint>
#include <vector>

// Global simulation time for Verilator
vluint64_t main_time = 0;
double sc_time_stamp() { return main_time; }

// Advance the clock: toggle low then high, incrementing time.
void toggle_clock(VRISCVAdderSubtractor32* dut) {
    dut->clock = 0;
    dut->eval();
    main_time += 5;
    dut->clock = 1;
    dut->eval();
    main_time += 5;
}

//---------------------------------------------------------------------
// For a 32-bit design we remove all other widths.
// We define opcodes (using the original bit encoding style) only for 32-bit:
//   - Bit[1:0] for width is fixed (2 for 32-bit)
//   - Bit2: 0 = unsigned, 1 = signed
//   - Bit3: 0 = add, 1 = subtract
//
// Thus we have:
enum Opcode : uint8_t {
    ADD_U32 = 2,   // 0010: unsigned add
    ADD_S32 = 6,   // 0110: signed add
    SUB_U32 = 10,  // 1010: unsigned subtract
    SUB_S32 = 14   // 1110: signed subtract
};

// A helper structure to hold a test vector: opcode, its signed-ness, and width.
struct TestVector {
    uint8_t opcode;
    bool isSigned; // true if signed (bit2==1)
    int width;     // will always be 32
};

// This function performs one test operation. It sets the DUT inputs,
// advances one clock cycle, and then compares the DUT outputs with expected values.
// Note that we use 32-bit types throughout.
void testOperation(VRISCVAdderSubtractor32* dut,
                   int32_t a, int32_t b,
                   uint32_t expected,
                   uint8_t opcode,
                   bool expectedFlag,         // for unsigned: expected carryOut; for signed: expected overflow
                   bool expectedZeroFlag,
                   bool expectedNegativeFlag,
                   bool printDebugInfo = false) {
    // Determine whether this test is signed (bit 2 of opcode)
    bool isSigned = ((opcode >> 2) & 1) == 1;

    // Set inputs. Casting to uint32_t ensures we pass the correct bit–pattern.
    dut->io_a = static_cast<uint32_t>(a);
    dut->io_b = static_cast<uint32_t>(b);
    dut->io_opcode = opcode;

    // Advance one clock cycle
    toggle_clock(dut);

    // Read outputs
    uint32_t result = dut->io_result;
    bool carryOut = (dut->io_carryOut != 0);
    bool overflowFlag = (dut->io_overflowFlag != 0);
    bool zeroFlag = (dut->io_zeroFlag != 0);
    bool negativeFlag = (dut->io_negativeFlag != 0);

    if (printDebugInfo) {
        std::cout << "[ALU] -- Result: 0x" << std::hex << result 
                  << ", Carry: " << std::dec << carryOut 
                  << ", zeroFlag: " << zeroFlag 
                  << ", negativeFlag: " << negativeFlag << std::endl;
    }

    // Check the computed result against expected
    if (result != expected) {
        std::cerr << "[ALU] -- Expected result 0x" << std::hex << expected 
                  << " but got 0x" << result 
                  << " for opcode " << std::dec << (int)opcode << std::endl;
        assert(false);
    }
    // For signed operations, check the overflow flag; for unsigned, check the carry-out.
    if (isSigned) {
        if (overflowFlag != expectedFlag) {
            std::cerr << "[ALU] -- Expected Overflow Flag " << expectedFlag 
                      << " but got " << overflowFlag 
                      << " for opcode " << (int)opcode << std::endl;
            assert(false);
        }
    } else {
        if (carryOut != expectedFlag) {
            std::cerr << "[ALU] -- Expected Carry Flag " << expectedFlag 
                      << " but got " << carryOut 
                      << " for opcode " << (int)opcode << std::endl;
            assert(false);
        }
    }
    if (zeroFlag != expectedZeroFlag) {
        std::cerr << "[ALU] -- Expected Zero Flag " << expectedZeroFlag 
                  << " but got " << zeroFlag 
                  << " for opcode " << (int)opcode << std::endl;
        assert(false);
    }
    if (negativeFlag != expectedNegativeFlag) {
        std::cerr << "[ALU] -- Expected Negative Flag " << expectedNegativeFlag 
                  << " but got " << negativeFlag 
                  << " for opcode " << (int)opcode << std::endl;
        assert(false);
    }
}

// This function runs a battery of arithmetic tests over the supported 32-bit opcodes.
void testArithmetic(VRISCVAdderSubtractor32* dut, bool printDebugInfo = false) {
    // Only test 32-bit operations.
    std::vector<TestVector> testVectors = {
        {ADD_U32, false, 32},
        {SUB_U32, false, 32},
        {ADD_S32, true, 32},
        {SUB_S32, true, 32}
    };

    for (const auto& tv : testVectors) {
        uint8_t opcode = tv.opcode;
        bool isSigned = tv.isSigned;
        int width = tv.width;
        // Determine if we are testing subtraction from the opcode (bit 3)
        bool isSub = ((opcode >> 3) & 1) == 1;
        bool isAdd = !isSub;

        // ----- Simple arithmetic: 4 + 3 or 4 - 3 -----
        uint32_t expected = (isSub ? (4 - 3) : (4 + 3));
        if (printDebugInfo) {
            std::cout << "[ALU] -- Testing opcode " << (int)opcode 
                      << " (width " << width << (isSigned ? ", signed" : ", unsigned") << ") with a=4, b=3"
                      << ", expecting " << expected << std::endl;
        }
        testOperation(dut, 4, 3, expected, opcode, false, false, false, printDebugInfo);

        // ----- Define constants for 32-bit corner-case tests -----
        // For unsigned operations:
        uint32_t maxValUnsigned = 0xFFFFFFFF;  // Maximum 32-bit unsigned value
        uint32_t minValUnsigned = 0;
        // For signed operations (using 32-bit two's complement):
        int32_t minValNegSigned = -(1 << 31);         // -2147483648
        int32_t maxValPosSigned = (1 << 31) - 1;        //  2147483647

        // ----- Unsigned operations: Overflow/Borrow -----
        if (!isSigned) {
            if (isAdd) {
                // Unsigned addition: max unsigned + 1 should wrap to 0.
                // Note: When passing maxValUnsigned as an int32_t, its bit–pattern is preserved.
                int32_t a = static_cast<int32_t>(maxValUnsigned);
                int32_t b = 1;
                uint32_t overflowExpected = minValUnsigned; // should wrap to 0
                bool overflowCarry = true;
                if (printDebugInfo) {
                    std::cout << "[ALU] -- Unsigned Add Overflow test (opcode " << (int)opcode << "): "
                              << a << " + " << b << " => 0x" << std::hex << overflowExpected 
                              << " with carry-out" << std::dec << std::endl;
                }
                testOperation(dut, a, b, overflowExpected, opcode, overflowCarry, true, false, printDebugInfo);
            }
            if (isSub) {
                // Unsigned subtraction: 0 - 1 should wrap to max unsigned.
                int32_t a = 0;
                int32_t b = 1;
                uint32_t borrowExpected = maxValUnsigned;
                bool borrowFlag = true;
                if (printDebugInfo) {
                    std::cout << "[ALU] -- Unsigned Sub Borrow test (opcode " << (int)opcode << "): "
                              << a << " - " << b << " => 0x" << std::hex << borrowExpected 
                              << " with borrow" << std::dec << std::endl;
                }
                testOperation(dut, a, b, borrowExpected, opcode, borrowFlag, false, false, printDebugInfo);
            }
        }

        // ----- Signed operations: Overflow/Underflow -----
        if (isSigned) {
            if (isAdd) {
                // Signed addition: max positive + 1 should overflow.
                int32_t a = maxValPosSigned;
                int32_t b = 1;
                // Expected result is the two's complement representation of -2147483648,
                // which as an unsigned value is 0x80000000.
                uint32_t overflowExpected = 0x80000000;
                bool overflowFlagExpected = true;
                if (printDebugInfo) {
                    std::cout << "[ALU] -- Signed Add Overflow test (opcode " << (int)opcode << "): "
                              << a << " + " << b << " => 0x" << std::hex << overflowExpected 
                              << " with overflow" << std::dec << std::endl;
                }
                testOperation(dut, a, b, overflowExpected, opcode, overflowFlagExpected, false, true, printDebugInfo);
            }
            if (isSub) {
                // Signed subtraction: min negative - 1 should underflow.
                int32_t a = minValNegSigned;
                int32_t b = 1;
                // Expected result wraps to max positive value.
                uint32_t underflowExpected = static_cast<uint32_t>(maxValPosSigned);
                bool underflowFlagExpected = true;
                if (printDebugInfo) {
                    std::cout << "[ALU] -- Signed Sub Underflow test (opcode " << (int)opcode << "): "
                              << a << " - " << b << " => 0x" << std::hex << underflowExpected 
                              << " with overflow" << std::dec << std::endl;
                }
                testOperation(dut, a, b, underflowExpected, opcode, underflowFlagExpected, false, false, printDebugInfo);
            }
        }

        // ----- Zero Flag tests -----
        // For unsigned subtraction: 5 - 5 should produce 0 with the zero flag set.
        if (!isSigned && isSub) {
            int32_t a = 5, b = 5;
            testOperation(dut, a, b, 0, opcode, false, true, false, printDebugInfo);
        }
        // For signed operations: 5 + (-5) (or 5 - 5) should yield zero.
        if (isSigned) {
            int32_t a = 5, b = (isAdd ? -5 : 5);
            testOperation(dut, a, b, 0, opcode, false, true, false, printDebugInfo);
        }
    }
}

int main(int argc, char **argv) {
    // Initialize Verilator and instantiate the DUT.
    Verilated::commandArgs(argc, argv);
    VRISCVAdderSubtractor32* dut = new VRISCVAdderSubtractor32;

    // Optionally, reset the DUT if your design supports a reset signal.
    // Example:
    // dut->reset = 1;
    // toggle_clock(dut);
    // dut->reset = 0;
    // toggle_clock(dut);

    bool printDebugInfo = false; // Change to true for more detailed output.
    testArithmetic(dut, printDebugInfo);

    std::cout << "All tests passed!" << std::endl;

    // Clean up and finish simulation.
    dut->final();
    delete dut;
    return 0;
}
