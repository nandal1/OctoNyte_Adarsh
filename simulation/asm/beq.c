#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#define SYS_WRITE 64
#define SYS_OPEN 56
#define SYS_CLOSE 57
#define SYS_EXIT 93
#define O_CREAT_WRONLY 1
#define FILE_MODE 0644
#define STDOUT_FILE "beq_dump.txt"

// Define structure for register state
typedef struct {
    unsigned long t0, t1, t2;
} RegisterState;

// Function to convert integer to string (manual formatting)
int int_to_str(char *buf, unsigned long value) {
    int i = 0;
    if (value == 0) {
        buf[i++] = '0';
    } else {
        char temp[20];
        int j = 0;
        while (value > 0) {
            temp[j++] = '0' + (value % 10);
            value /= 10;
        }
        while (j > 0) {
            buf[i++] = temp[--j];
        }
    }
    buf[i] = '\n'; // Add newline at the end
    buf[i + 1] = '\0';
    return i + 1;
}

// Function to write to file using syscalls
void save_register_dump(const RegisterState *state) {
    int fd;
    char buffer[256];
    int len = 0;

    // Open file for writing
    __asm__ volatile (
        "li a7, %0\n"
        "li a0, -100\n" // AT_FDCWD
        "mv a1, %1\n"
        "li a2, %2\n"
        "li a3, %3\n"
        "ecall\n"
        "mv %0, a0\n"
        : "=r"(fd)
        : "r"(STDOUT_FILE), "r"(O_CREAT_WRONLY), "r"(FILE_MODE)
        : "a0", "a1", "a2", "a3", "a7"
    );

    if (fd < 0) return;

    // Format output manually
    len += int_to_str(buffer + len, state->t0);
    len += int_to_str(buffer + len, state->t1);
    len += int_to_str(buffer + len, state->t2);

    // Write to file using syscall
    __asm__ volatile (
        "li a7, %0\n"
        "mv a0, %1\n"
        "mv a1, %2\n"
        "mv a2, %3\n"
        "ecall\n"
        :
        : "r"(SYS_WRITE), "r"(fd), "r"(buffer), "r"(len)
        : "a0", "a1", "a2", "a7"
    );

    // Close file using syscall
    __asm__ volatile (
        "li a7, %0\n"
        "mv a0, %1\n"
        "ecall\n"
        :
        : "r"(SYS_CLOSE), "r"(fd)
        : "a0", "a7"
    );
}

// Function to fetch register values using RISC-V inline assembly
void get_registers(RegisterState *regs) {
    __asm__ volatile (
        "mv %0, t0\n"
        "mv %1, t1\n"
        "mv %2, t2\n"
        : "=r"(regs->t0), "=r"(regs->t1), "=r"(regs->t2)
    );
}

// Program entry point (_start)
void _start() {
    RegisterState state;

    // Initialize registers
    state.t0 = 10;
    state.t1 = 10;

    get_registers(&state);

    // ✅ BEQ instruction: branch if equal
    __asm__ volatile (
        "beq t0, t1, 1f\n"    // Branch to label 1f if t0 == t1
        "addi t2, zero, 0\n"  // Won't execute if branch is taken
        "j 2f\n"              // Jump to label 2f (skip next instruction)
        "1:\n"
        "addi t2, zero, 1\n"  // Executes if branch is taken
        "2:\n"
    );

    get_registers(&state);

    // ✅ Save register state to file
    save_register_dump(&state);

    // ✅ Exit syscall
    __asm__ volatile (
        "li a7, %0\n"
        "li a0, 0\n"
        "ecall\n"
        :
        : "i"(SYS_EXIT)
        : "a0", "a7"
    );
}
