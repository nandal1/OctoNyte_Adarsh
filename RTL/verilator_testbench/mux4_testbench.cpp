#include "VMux4.h" // Verilated top module
#include "verilated.h"
#include "verilated_vcd_c.h" 
#include <iostream>

int main(int argc, char** argv) {
    Verilated::commandArgs(argc, argv);
    VMux4* top = new VMux4;

    // Reset the design
    top->clock = 0;
    top->reset = 1;
    top->eval();
    top->reset = 0;
    
    // Test inputs
    top->io_inputs_0 = 8'hAA;
    top->io_inputs_1 = 8'hBB;
    top->io_inputs_2 = 8'hCC;
    top->io_inputs_3 = 8'hDD;
    
    for (int i = 0; i < 4; i++) {
        top->io_select = i;
        top->eval();
        std::cout << "Select: " << i << " Output: " << std::hex << (int)top->io_output << std::endl;
    }

    delete top;
    return 0;
}
