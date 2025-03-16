#include <stdint.h>

#define SYS_WRITE 64
#define SYS_EXIT 93
#define SYS_OPENAT 56
#define SYS_CLOSE 57
#define AT_FDCWD -100
#define OUTPUT_FILE "xor_dump.txt"

// Define structure for register state
typedef struct {
    unsigned long ra, sp, gp, tp;
    unsigned long t0, t1, t2;
    unsigned long s0, s1;
    unsigned long a0, a1, a2, a3, a4, a5, a6, a7;
    unsigned long s2, s3, s4, s5, s6, s7, s8;
    unsigned long s9, s10, s11;
    unsigned long t3, t4, t5, t6;
} RegisterState;

// Function to save register dump to a text file using RISC-V syscalls
void save_register_dump(const RegisterState *state) {
    int fd;
    char buffer[512];
    int len;

    // Open file using syscall
    __asm__ volatile (
        "li a7, %0\n"         // syscall for openat
        "li a0, %1\n"         // AT_FDCWD
        "mv a1, %2\n"         // filename
        "li a2, 1\n"          // O_WRONLY
        "li a3, 0644\n"       // Permissions
        "ecall\n"
        "mv %0, a0\n"
        : "=r"(fd)
        : "i"(AT_FDCWD), "r"(OUTPUT_FILE)
        : "a0", "a1", "a2", "a3", "a7"
    );

    if (fd < 0) return;

    // Format the register values into a buffer
    len = snprintf(buffer, sizeof(buffer),
        "Register State:\n"
        "ra=%lu sp=%lu gp=%lu tp=%lu\n"
        "t0=%lu t1=%lu t2=%lu s0=%lu s1=%lu\n"
        "a0=%lu a1=%lu a2=%lu a3=%lu a4=%lu a5=%lu a6=%lu a7=%lu\n"
        "s2=%lu s3=%lu s4=%lu s5=%lu s6=%lu s7=%lu s8=%lu s9=%lu s10=%lu s11=%lu\n"
        "t3=%lu t4=%lu t5=%lu t6=%lu\n\n",
        state->ra, state->sp, state->gp, state->tp,
        state->t0, state->t1, state->t2, state->s0, state->s1,
        state->a0, state->a1, state->a2, state->a3, state->a4, state->a5, state->a6, state->a7,
        state->s2, state->s3, state->s4, state->s5, state->s6, state->s7, state->s8, state->s9, state->s10, state->s11,
        state->t3, state->t4, state->t5, state->t6
    );

    // Write buffer to file using syscall
    __asm__ volatile (
        "li a7, %0\n"         // syscall for write
        "mv a0, %1\n"
        "mv a1, %2\n"
        "mv a2, %3\n"
        "ecall\n"
        :
        : "i"(SYS_WRITE), "r"(fd), "r"(buffer), "r"(len)
        : "a0", "a1", "a2", "a7"
    );

    // Close file using syscall
    __asm__ volatile (
        "li a7, %0\n"         // syscall for close
        "mv a0, %1\n"
        "ecall\n"
        :
        : "i"(SYS_CLOSE), "r"(fd)
        : "a0", "a7"
    );
}

// Function to capture register state using inline RISC-V assembly
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
        : "=r"(regs->ra), "=r"(regs->sp), "=r"(regs->gp), "=r"(regs->tp),
          "=r"(regs->t0), "=r"(regs->t1), "=r"(regs->t2), "=r"(regs->s0),
          "=r"(regs->s1), "=r"(regs->a0), "=r"(regs->a1), "=r"(regs->a2),
          "=r"(regs->a3), "=r"(regs->a4), "=r"(regs->a5), "=r"(regs->a6)
    );
}

// ✅ _start is defined with __attribute__((naked))
__attribute__((naked)) void _start() {
    RegisterState state;

    // Initialize registers with sample values
    state.t0 = 0xAAAA;
    state.t1 = 0x5555;

    // Load values into registers using inline assembly
    __asm__ volatile (
        "mv t0, %0\n"
        "mv t1, %1\n"
        :
        : "r"(state.t0), "r"(state.t1)
    );

    // Capture initial register state
    get_registers(&state);
    save_register_dump(&state);

    // Perform XOR operation: t2 = t0 ^ t1
    __asm__ volatile (
        "xor t2, t0, t1\n"
    );

    // Capture state after XOR
    get_registers(&state);
    save_register_dump(&state);

    // Exit syscall
    __asm__ volatile (
        "li a7, %0\n"         // syscall for exit
        "li a0, 0\n"          // exit code 0
        "ecall\n"
        :
        : "i"(SYS_EXIT)
        : "a0", "a7"
    );
}
