 #include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#define MAX_DUMPS 100  // Maximum number of state dumps

typedef struct {
    int ra, sp, gp, tp;
    int t0, t1, t2;
    int s0, s1;
    int a0, a1, a2, a3, a4, a5, a6, a7;
    int s2, s3, s4, s5, s6, s7, s8;
    int s9, s10, s11;
    int t3, t4, t5, t6;
} RegisterState;

typedef struct {
    RegisterState states[MAX_DUMPS];
    int count;
} RegisterDump;

void save_register_dump(const RegisterDump *dump, const char *filename) {
    FILE *file = fopen(filename, "w");
    if (!file) {
        perror("Failed to open file");
        return;
    }

    for (int i = 0; i < dump->count; i++) {
        RegisterState state = dump->states[i];
        fprintf(file, "Dump %d:\n", i + 1);
        fprintf(file, "ra=%d sp=%d gp=%d tp=%d\n", state.ra, state.sp, state.gp, state.tp);
        fprintf(file, "t0=%d t1=%d t2=%d s0=%d s1=%d\n", state.t0, state.t1, state.t2, state.s0, state.s1);
        fprintf(file, "a0=%d a1=%d a2=%d a3=%d a4=%d a5=%d a6=%d a7=%d\n",
                state.a0, state.a1, state.a2, state.a3, state.a4, state.a5, state.a6, state.a7);
        fprintf(file, "s2=%d s3=%d s4=%d s5=%d s6=%d s7=%d s8=%d s9=%d s10=%d s11=%d\n",
                state.s2, state.s3, state.s4, state.s5, state.s6, state.s7, state.s8, state.s9, state.s10, state.s11);
        fprintf(file, "t3=%d t4=%d t5=%d t6=%d\n\n", state.t3, state.t4, state.t5, state.t6);
    }

    fclose(file);
}

void get_registers(RegisterState *regs) {
    // Initialize some sample register values
    regs->ra = 0;
    regs->sp = 1;
    regs->gp = 2;
    regs->tp = 3;
    regs->t0 = 4;
    regs->t1 = 5;
    regs->t2 = 6;
    regs->s0 = 7;
    regs->s1 = 8;
    regs->a0 = 9;
    regs->a1 = 10;
    regs->a2 = 11;
    regs->a3 = 12;
    regs->a4 = 13;
    regs->a5 = 14;
    regs->a6 = 15;
    regs->a7 = 16;
    regs->s2 = 17;
    regs->s3 = 18;
    regs->s4 = 19;
    regs->s5 = 20;
    regs->s6 = 21;
    regs->s7 = 22;
    regs->s8 = 23;
    regs->s9 = 24;
    regs->s10 = 25;
    regs->s11 = 26;
    regs->t3 = 27;
    regs->t4 = 28;
    regs->t5 = 29;
    regs->t6 = 30;
}

int main() {
    RegisterDump regs;
    regs.count = 0;

    RegisterState state = {0};

    // Get initial register state
    get_registers(&state);
    regs.states[regs.count++] = state;

    // Simulate BEQ instruction
    printf("Testing BEQ instruction...\n");

    state.s2 = 1; // Set register s2 to 1
    state.s3 = 1; // Set register s3 to 1

    if (state.s2 == state.s3) {
        // If equal, branch is taken — set s4 to 1
        state.s4 = 1;
        printf("BEQ: s2 == s3, branch taken. s4 = %d\n", state.s4);
    } else {
        // If not equal, branch not taken — set s4 to 0
        state.s4 = 0;
        printf("BEQ: s2 != s3, branch not taken. s4 = %d\n", state.s4);
    }

    // Save the state after BEQ
    get_registers(&state);
    regs.states[regs.count++] = state;

    // Save register dump to file
    save_register_dump(&regs, "beq_dump.txt");

    printf("\nRegister dump saved to 'beq_dump.txt'\n");

    return 0;
}
