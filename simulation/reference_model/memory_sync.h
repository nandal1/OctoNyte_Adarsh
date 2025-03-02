#ifndef MEMORY_SYNC_H
#define MEMORY_SYNC_H

#include <unordered_map>

class MemorySync
{
public:
    void write_mem(uint64_t addr, uint64_t value)
    {
        memory[addr] = value;
    }

    uint64_t read_mem(uint64_t addr)
    {
        return memory.count(addr) ? memory[addr] : 0;
    }

private:
    std::unordered_map<uint64_t, uint64_t> memory;
};

#endif
