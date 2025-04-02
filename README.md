# Parameterized RISC-V Execution Units (Chisel)

## Project Description
As a CPU Designer working on a 32-bit RISC-V processor, I want to develop parameterized execution units (ALU, Load, Store, and Multiplier) in Chisel so that the hardware design can support a range of configurations, remain compliant with RISC-V specifications, and be easily reused in future projects.

## Acceptance Criteria

### RISC-V 32-bit Compliance
- Must adhere to the base RISC-V RV32I instruction set requirements (e.g., 32-bit operations for arithmetic, load, and store)
- Parameterizable design should allow easy expansion or integration with optional RISC-V extensions if needed (e.g., multiplier)

### Parameter Configurability
- Each execution unit supports configurable options (e.g., pipeline depth, optional extra functionalities) without extensive code modifications
- Changes to parameters automatically propagate through Chisel modules and generate corresponding Verilog

### Synthesizable Output
- Generated Verilog must be synthesizable and meet timing for 32-bit RISC-V designs
- Clear and consistent naming conventions for SoC integration

### Reusability & Modularity
- Encapsulated in distinct Chisel modules with well-defined interfaces
- Can be dropped into future RISC-V processor designs with minimal changes

### Robust Testing & Validation
- Test benches cover typical and edge-case RISC-V instructions
- Independent unit tests plus system-level validation

## Definition of Done
✅ **RISC-V Instruction Set Coverage**: All relevant 32-bit RV32I instructions supported  
✅ **Chisel & Verilog Generation**: Error-free compilation and synthesis  
✅ **Parameter Testing**: All configurations verified functional  
✅ **Documentation**: Clear parameter usage and integration examples  
✅ **Performance**: Meets target timing constraints  

## Implementation Tasks

### CPU 1: ALU (Arithmetic Logic Unit)
**Subtask A1**: Implement basic arithmetic operations (ADD, SUB) and logical operations (AND, OR, XOR) according to RV32I.  
**Subtask A2**: Parameterize ALU features (e.g., optional pipeline stage, bitmasking, additional operations).  
**Subtask A3**: Integrate testing for all ALU operations, verifying edge cases like zero, sign extension, and overflow.  

### CPU 2: Load Unit
**Subtask B1**: Implement support for RISC-V load instructions (LB, LH, LW, LBU, LHU).  
**Subtask B2**: Parameterize load width and sign-extension features to accommodate future expansions (e.g., half-word loads).  
**Subtask B3**: Create a functional test bench to confirm correct data alignment, sign extension, and memory interface handling.  

### CPU 3: Store Unit
**Subtask C1**: Implement store instructions (SB, SH, SW) following RISC-V alignment and data-size rules.  
**Subtask C2**: Parameterize store width to match load-unit configurations and ensure consistent alignment checks.  
**Subtask C3**: Test store operations with edge-case addresses and verify memory interface timing.  

### CPU 4: Branch Unit
**Subtask D1**: Implement branch instructions (BEQ, BNE, BLT, BGE) following RISC-V PC-relative addressing and comparison rules.  
**Subtask D2**: Parameterize branch prediction (static/dynamic) to match pipeline depth and ensure consistent flush behavior.  
**Subtask D3**: Test branch operations with edge-case offsets (min/max, misaligned) and verify pipeline flush timing. 
