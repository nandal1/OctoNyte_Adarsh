#include <stdint.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#define OUTPUT_FILE "jal_dump.txt"

// Structure to hold register values
typedef struct {
    uint64_t ra;  // Return address (ra) register
    uint64_t t0;
    uint64_t t1;
} RegisterState;

// Function to capture register values
void get_registers(RegisterState *regs) {
    __asm__ volatile (
        "mv %0, ra\n"
        "mv %1, t0\n"
        "mv %2, t1\n"
        : "=r"(regs->ra), "=r"(regs->t0), "=r"(regs->t1)
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
        "ra = 0x%lx\n"
        "t0 = 0x%lx\n"
        "t1 = 0x%lx\n",
        state->ra, state->t0, state->t1
    );

    ssize_t bytes_written = write(fd, buffer, len);
    if (bytes_written < 0) {
        perror("Error writing to file");
    }

    close(fd);
}

// Function to be called via `jal`
void function_label() {
    __asm__ volatile (
        "li t0, 0x1234\n"   // Load immediate 0x1234 into t0
        "li t1, 0x5678\n"   // Load immediate 0x5678 into t1
        "ret\n"             // Return from function
    );
}

int main() {
    RegisterState state;

    // Using `jal` to jump to `function_label` and save return address in `ra`
    __asm__ volatile (
        "jal ra, function_label\n"
    );

    // Capture register state after calling `jal`
    get_registers(&state);
    save_register_dump(&state);

    return 0;
}
