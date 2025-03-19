#include <stdint.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#define OUTPUT_FILE "lb_dump.txt"

// ✅ Declare `data` globally to prevent `undefined reference` error
volatile static uint8_t data = 0xAB;  // Sample byte in memory

// Structure to hold register values
typedef struct {
    uint64_t address;
    int8_t loaded_byte;
} RegisterState;

// Function to capture register values
void get_registers(RegisterState *regs) {
    __asm__ volatile (
        "mv %0, a0\n"   // Save memory address
        "mv %1, a1\n"   // Save loaded byte
        : "=r"(regs->address), "=r"(regs->loaded_byte)
    );
}

// Function to save register dump to a file
void save_register_dump(const RegisterState *state) {
    int fd = open(OUTPUT_FILE, O_WRONLY | O_CREAT | O_TRUNC, 0644);
    if (fd < 0) {
        perror("Error opening file");
        return;
    }

    char buffer[128];
    int len = snprintf(buffer, sizeof(buffer),
        "Register Dump:\n"
        "Memory Address = 0x%lx\n"
        "Loaded Byte = 0x%x (%d)\n",
        state->address, state->loaded_byte, state->loaded_byte
    );

    ssize_t bytes_written = write(fd, buffer, len);
    if (bytes_written < 0) {
        perror("Error writing to file");
    }

    close(fd);
}

// ✅ Function to perform `lb`
void load_byte() {
    __asm__ volatile (
        "lla a0, data\n"  // Load address of `data` into a0 (position-independent)
        "lb a1, 0(a0)\n"  // Load byte from memory address in a0 into a1
        : 
        : "r"(data)       // Inform the compiler about `data`
        : "a0", "a1", "memory"  // Clobber memory to prevent optimization issues
    );
}

int main() {
    RegisterState state;

    // ✅ Call function that executes `lb`
    load_byte();

    // ✅ Capture register state after executing `lb`
    get_registers(&state);
    save_register_dump(&state);

    return 0;
}