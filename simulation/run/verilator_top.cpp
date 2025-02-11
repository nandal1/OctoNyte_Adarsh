#include "verilator_interface.h"
#include <iostream>

int main()
{
    VerilatorInterface rtlSim;

    // Simulate a few cycles
    for (int i = 0; i < 10; i++)
    {
        rtlSim.step();
        std::cout << "Cycle " << i << " Output: " << rtlSim.get_output(0) << std::endl;
    }

    return 0;
}
