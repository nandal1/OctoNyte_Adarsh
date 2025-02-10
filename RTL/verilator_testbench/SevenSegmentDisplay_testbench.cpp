#include <iostream>
#include <vector>
#include "VSevenSegmentDisplay.h"  // Verilator-generated top-level header
#include "verilated.h"

using std::cout;
using std::endl;
using std::vector;

int main(int argc, char **argv) {
    // Pass command-line arguments to Verilator.
    Verilated::commandArgs(argc, argv);

    // Instantiate the DUT (Device Under Test)
    VSevenSegmentDisplay* dut = new VSevenSegmentDisplay;

    // Expected outputs for each input (0-9) as defined in your Chisel test:
    // "b1111110" = 126, "b0110000" = 48, "b1101101" = 109, "b1111001" = 121,
    // "b0110011" = 51,  "b1011011" = 91,  "b1011111" = 95,  "b1110000" = 112,
    // "b1111111" = 127, "b1111011" = 123
    vector<uint32_t> expectedOutputs = {126, 48, 109, 121, 51, 91, 95, 112, 127, 123};

    bool errorOccurred = false;

    cout << "Starting SevenSegmentDisplay tests..." << endl;

    // Test all inputs from 0 to 9.
    for (uint32_t inputVal = 0; inputVal < expectedOutputs.size(); ++inputVal) {
        // Set the binary input.
        dut->io_binIn = inputVal;
        
        // Toggle the clock to allow the combinational logic to propagate.
        dut->clock = 0;
        dut->eval();
        dut->clock = 1;
        dut->eval();

        // Read the output value.
        uint32_t actualOutput = dut->io_segOut;

        // Compare against the expected output.
        if (actualOutput != expectedOutputs[inputVal]) {
            cout << "Error: For input " << inputVal 
                 << ", expected 0x" << std::hex << expectedOutputs[inputVal]
                 << " but got 0x" << actualOutput << std::dec << endl;
            errorOccurred = true;
        } else {
            cout << "Pass: Input " << inputVal 
                 << " produced expected output 0x" << std::hex << actualOutput << std::dec << endl;
        }
    }

    // Final summary.
    if (errorOccurred) {
        cout << "\nSome tests FAILED." << endl;
    } else {
        cout << "\nAll tests PASSED successfully." << endl;
    }

    // Final model cleanup.
    dut->final();
    delete dut;
    return errorOccurred ? 1 : 0;
}