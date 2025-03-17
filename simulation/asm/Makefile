# RISC-V cross compiler
CC = riscv64-linux-gnu-gcc

# Compiler flags: static linking, optimization, and all warnings enabled.
CFLAGS = -static -O2 -Wall

# List of target executables
TARGETS = test_addi test_nand test_sub  test_nor test_xnor save_register_dump test_lui test_auipc test_jal test_jalr test_beq test_blt test_bge test_bltu test_bgeu test_lb test_lh test_lw test_lbu test_lhu test_sb test_sh test_sw

# Directory to store the compiled binaries
BIN_DIR = bin

# Ensure bin directory exists before placing compiled files
$(shell mkdir -p $(BIN_DIR))

# Default target: build all executables
all: $(TARGETS)

# Build test_sw from test_sw.c
test_sw: test_sw.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_sw test_sw.c 

# Build test_sh from test_sh.c
test_sh: test_sh.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_sh test_sh.c 

# Build test_sb from test_sb.c
test_sb: test_sb.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_sb test_sb.c 

# Build test_lhu from test_lhu.c
test_lhu: test_lhu.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_lhu test_lhu.c 

# Build test_lw from test_lw.c
test_lbu: test_lbu.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_lbu test_lbu.c 

# Build test_lw from test_lw.c
test_lw: test_lw.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_lw test_lw.c 

# Build test_lh from test_lh.c
test_lh: test_lh.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_lh test_lh.c 

# Build test_lb from test_lb.c
test_lb: test_lb.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_lb test_lb.c 

# Build test_bgeu from test_bgeu.c
test_bgeu: test_bgeu.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_bgeu test_bgeu.c 

# Build test_bltu from test_bltu.c
test_bltu: test_bltu.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_bltu test_bltu.c 

# Build test_bge from test_bge.c
test_bge: test_bge.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_bge test_bge.c 

# Build test_bltfrom test_blt.c
test_blt: test_blt.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_blt test_blt.c 

# Build test_be	 from test_beq.c
test_beq: test_beq.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_beq test_beq.c 

# Build test_jalr from test_jalr.c
test_jalr: test_jalr.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_jalr test_jalr.c 

# Build test_auipc from test_auipc.c
test_jal: test_jal.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_jal test_jal.c 
# Build test_auipc from test_auipc.c
test_auipc: test_auipc.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_auipc test_auipc.c 

# Build test_xnor from test_xnor.c
test_lui: test_lui.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_lui test_lui.c 

# Build test_xnor from test_xnor.c
save_register_dump: save_register_dump.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/save_register_dump save_register_dump.c  


# Build test_xnor from test_xnor.c
test_xnor: test_xnor.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_xnor test_xnor.c 

# Build test_nor from test_nor.c
test_nor: test_nor.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_nor test_nor.c 


# Build test_sub from test_sub.c
test_sub: test_sub.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_sub test_sub.c

# Build test_nand from test_nand.c
test_nand: test_nand.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_nand test_nand.c

# Build test_addi from test_addi.c
test_addi: test_addi.c
	$(CC) $(CFLAGS) -o $(BIN_DIR)/test_addi test_addi.c

# Clean up build artifacts
clean:
	rm -f $(BIN_DIR)/*

.PHONY: all clean
