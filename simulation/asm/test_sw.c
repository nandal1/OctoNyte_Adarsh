#include <stdint.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#define OUTPUT_FILE "sw_dump.txt"

// Structure to hold memory address and stored value
typedef struct {
    uint64_t address;
    int32_t stored_value;
} RegisterState;

// Function to capture memory address and stored value
void get_registers(RegisterState *regs) {
    __asm__ volatile (
        "mv %0, a0\n"   // Store memory address
        "mv %1, a1\n"   // Store stored value
        : "=r"(regs->address), "=r"(regs->stored_value)
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
        "Stored Value = 0x%x (%d)\n",
        state->address, state->stored_value, state->stored_value
    );

    ssize_t bytes_written = write(fd, buffer, len);
    if (bytes_written < 0) {
        perror("Error writing to file");
    }

    close(fd);
}

// Function to perform `sw`
void store_word() {
    static int32_t data = 0;  // Memory location to store the word

    __asm__ volatile (
        "li a1, 0x12345678\n"  // Load immediate value into a1 (word to store)
        "la a0, data\n"         // Load address of `data` into a0
        "sw a1, 0(a0)\n"        // Store word in memory address in a0
        "ret\n"                 // Return from function
    );
}

int main() {
    RegisterState state;

    // Call the function that executes `sw`
    __asm__ volatile (
        "call store_word\n"
    );

    // Capture register state after executing `sw`
    get_registers(&state);
    save_register_dump(&state);

    return 0;
}
