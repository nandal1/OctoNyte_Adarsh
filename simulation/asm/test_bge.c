#include <stdint.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#define OUTPUT_FILE "bge_dump.txt"

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

// Function to be used with `bge`
void compare_values() {
    __asm__ volatile (
        "li t0, 15\n"          // Load 15 into t0
        "li t1, 10\n"          // Load 10 into t1
        "bge t0, t1, greater_equal\n"  // If t0 >= t1, jump to greater_equal
        "li a0, 0\n"           // If t0 < t1, set a0 = 0
        "j end\n"              // Jump to end

        "greater_equal:\n"
        "li a0, 1\n"           // If t0 >= t1, set a0 = 1

        "end:\n"
        "ret\n"                // Return from function
    );
}

int main() {
    RegisterState state;

    // Call the function that executes `bge`
    __asm__ volatile (
        "call compare_values\n"
    );

    // Capture register state after calling `bge`
    get_registers(&state);
    save_register_dump(&state);

    return 0;
}
