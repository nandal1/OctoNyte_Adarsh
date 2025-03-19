#include <stdint.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#define OUTPUT_FILE "xor_dump.txt"

// Structure to hold register values
typedef struct {
    uint64_t ra, sp, gp, tp;
    uint64_t t0, t1, t2;
    uint64_t s0, s1;
    uint64_t a0, a1, a2, a3, a4, a5, a6, a7;
    uint64_t s2, s3, s4, s5, s6, s7, s8, s9, s10, s11;
    uint64_t t3, t4, t5, t6;
} RegisterState;

// Function to capture registers
void get_registers(RegisterState *regs) {
    __asm__ volatile (
        "mv %0, ra\n"
        "mv %1, sp\n"
        "mv %2, gp\n"
        "mv %3, tp\n"
        "mv %4, t0\n"
        "mv %5, t1\n"
        "mv %6, t2\n"
        "mv %7, s0\n"
        "mv %8, s1\n"
        "mv %9, a0\n"
        "mv %10, a1\n"
        "mv %11, a2\n"
        "mv %12, a3\n"
        "mv %13, a4\n"
        "mv %14, a5\n"
        "mv %15, a6\n"
        "mv %16, a7\n"
        : "=r"(regs->ra), "=r"(regs->sp), "=r"(regs->gp), "=r"(regs->tp),
          "=r"(regs->t0), "=r"(regs->t1), "=r"(regs->t2), "=r"(regs->s0),
          "=r"(regs->s1), "=r"(regs->a0), "=r"(regs->a1), "=r"(regs->a2),
          "=r"(regs->a3), "=r"(regs->a4), "=r"(regs->a5), "=r"(regs->a6),
          "=r"(regs->a7)
    );
}

// Function to save register dump
void save_register_dump(const RegisterState *state) {
    int fd = open(OUTPUT_FILE, O_WRONLY | O_CREAT | O_TRUNC, 0644);
    if (fd < 0) {
        perror("Error opening file");
        return;
    }

    char buffer[512];
    int len = snprintf(buffer, sizeof(buffer),
        "Register Dump:\n"
        "ra=%lx sp=%lx gp=%lx tp=%lx\n"
        "t0=%lx t1=%lx t2=%lx s0=%lx s1=%lx\n"
        "a0=%lx a1=%lx a2=%lx a3=%lx a4=%lx a5=%lx a6=%lx a7=%lx\n",
        state->ra, state->sp, state->gp, state->tp,
        state->t0, state->t1, state->t2, state->s0, state->s1,
        state->a0, state->a1, state->a2, state->a3, state->a4, state->a5, state->a6, state->a7
    );

    ssize_t bytes_written = write(fd, buffer, len);
    if (bytes_written < 0) {
        perror("Error writing to file");
    }

    close(fd);
}

int main() {
    RegisterState state;

    // Set initial values
    state.t0 = 0xAAAA;
    state.t1 = 0x5555;

    // Load values into registers
    __asm__ volatile (
        "mv t0, %[val1]\n"
        "mv t1, %[val2]\n"
        :
        : [val1] "r"(state.t0), [val2] "r"(state.t1)
    );

    // Capture state before XOR
    get_registers(&state);
    save_register_dump(&state);

    // Perform XOR operation
    __asm__ volatile ("xor t2, t0, t1\n");

    // Capture state after XOR
    get_registers(&state);
    save_register_dump(&state);

    return 0;
}
