#include <stdint.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#define OUTPUT_FILE "bltu_dump.txt"

// Structure to hold register values
typedef struct {
    uint64_t t0;
    uint64_t t1;
    uint64_t result;
} RegisterState;

// Function to capture register values
void get_registers(RegisterState *regs) {
    __asm__ volatile (
        "mv %0, t0\n"
        "mv %1, t1\n"
        "mv %2, a0\n"
        : "=r"(regs->t0), "=r"(regs->t1), "=r"(regs->result)
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
        "t0 = 0x%lx\n"
        "t1 = 0x%lx\n"
        "result = 0x%lx\n",
        state->t0, state->t1, state->result
    );

    ssize_t bytes_written = write(fd, buffer, len);
    if (bytes_written < 0) {
        perror("Error writing to file");
    }

    close(fd);
}

// Function to be used with `bltu`
void compare_unsigned_values() {
    __asm__ volatile (
        "li t0, 10\n"          // Load unsigned 10 into t0
        "li t1, 15\n"          // Load unsigned 15 into t1
        "bltu t0, t1, less_unsigned\n"  // If t0 < t1 (unsigned), jump to less_unsigned
        "li a0, 0\n"           // If t0 >= t1 (unsigned), set a0 = 0
        "j end\n"              // Jump to end

        "less_unsigned:\n"
        "li a0, 1\n"           // If t0 < t1 (unsigned), set a0 = 1

        "end:\n"
        "ret\n"                // Return from function
    );
}

int main() {
    RegisterState state;

    // Call the function that executes `bltu`
    __asm__ volatile (
        "call compare_unsigned_values\n"
    );

    // Capture register state after calling `bltu`
    get_registers(&state);
    save_register_dump(&state);

    return 0;
}
