#ifndef REGISTER_SYNC_H
#define REGISTER_SYNC_H

#include <vector>

class RegisterSync
{
public:
    RegisterSync()
    {
        registers.resize(32, 0);
    }

    void write_reg(int reg, uint64_t value)
    {
        if (reg >= 0 && reg < 32)
            registers[reg] = value;
    }

    uint64_t read_reg(int reg)
    {
        return (reg >= 0 && reg < 32) ? registers[reg] : 0;
    }

private:
    std::vector<uint64_t> registers;
};

#endif
