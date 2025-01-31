#include <verilated.h>
#include "VSevenSegmentDisplay.h"  // Include the generated header file
#include <iostream>

int main(int argc, char** argv) {
    Verilated::commandArgs(argc, argv);
    VSevenSegmentDisplay* top = new VSevenSegmentDisplay;

    // Reset sequence
    top->reset = 1;
    top->clock = 0;
    top->eval();
    top->reset = 0;

    // Test all 4-bit binary inputs
    for (int i = 0; i < 16; ++i) {
        top->io_binIn = i;
        top->eval();

        // Print the output
        std::cout << "Binary Input: " << i << " => 7-Segment Output: "
                  << std::hex << static_cast<int>(top->io_segOut) << std::dec << std::endl;
    }

    delete top;
    return 0;
}

