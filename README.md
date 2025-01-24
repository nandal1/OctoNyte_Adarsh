
# OctoNyte
A RISC-V Multithreaded Core Targeting the Skywater 0.13um ASIC library

## User Story: CPU Designer - Parameterized Execution Units in Chisel  

### Description  
As a CPU Designer working on a 32-bit RISC-V processor, I want to develop parameterized execution units (ALU, Load, Store, and Multiplier) in Chisel so that the hardware design can support a range of configurations, remain compliant with RISC-V specifications, and be easily reused in future projects.

---

## Acceptance Criteria  
1. **RISC-V 32-bit Compliance**  
   - [ ] Must adhere to the base RISC-V RV32I instruction set requirements (e.g., 32-bit operations for arithmetic, load, and store).  
   - [ ] Parameterizable design should allow easy expansion or integration with optional RISC-V extensions if needed (e.g., multiplier).  

2. **Parameter Configurability**  
   - [ ] Each execution unit (ALU, Load, Store, Multiplier) supports configurable options (e.g., pipeline depth, optional extra functionalities) without requiring extensive code modifications.  
   - [ ] Changes to parameters automatically propagate through the Chisel modules and generate the corresponding Verilog.  

3. **Synthesizable Output**  
   - [ ] The generated Verilog for each execution unit must be synthesizable and verified to meet timing for 32-bit RISC-V designs.  
   - [ ] Clear and consistent naming conventions must be used for integration into larger SoC projects.  

4. **Reusability & Modularity**  
   - [ ] Execution units are encapsulated in distinct Chisel modules with well-defined interfaces.  
   - [ ] Each module (ALU, Load, Store, Multiplier) can be dropped into future RISC-V processor designs or adapted for similar architectures with minimal changes.  

5. **Robust Testing & Validation**  
   - [ ] Test benches must cover typical and edge-case RISC-V instructions for the 32-bit architecture (e.g., boundary conditions, overflow scenarios).  
   - [ ] Each execution unit is verified independently (unit tests) and then integrated into a larger test environment for system-level validation.  

---

## Definition of Done  
- [ ] **RISC-V Instruction Set Coverage:** The parameterized ALU, Load, Store, and Multiplier units correctly handle all relevant 32-bit RISC-V RV32I instructions they are responsible for.  
- [ ] **Chisel & Verilog Generation:** Code compiles in Chisel, and generated Verilog is synthesizable with no errors or critical warnings.  
- [ ] **Parameter Testing:** All configurable parameters (e.g., pipeline stages, optional multiplier presence) are tested, and the design functions correctly in each configuration.  
- [ ] **Documentation & Examples:** Implementation details, parameter usage, and integration steps are documented. Examples (e.g., test fixture or reference project) show how to incorporate the parameterized modules into a broader CPU design.  
- [ ] **Performance & Timing:** The design meets target frequency or timing constraints for a 32-bit RISC-V CPU without critical performance bottlenecks.  

---

## Implementation Tasks  

### Task A: ALU (Arithmetic Logic Unit)  
- [ ] **Subtask A1:** Implement basic arithmetic operations (ADD, SUB) and logical operations (AND, OR, XOR) according to RV32I.  
- [ ] **Subtask A2:** Parameterize ALU features (e.g., optional pipeline stage, bitmasking, additional operations).  
- [ ] **Subtask A3:** Integrate testing for all ALU operations, verifying edge cases like zero, sign extension, and overflow.  

### Task B: Load Unit  
- [ ] **Subtask B1:** Implement support for RISC-V load instructions (LB, LH, LW, LBU, LHU).  
- [ ] **Subtask B2:** Parameterize load width and sign-extension features to accommodate future expansions (e.g., half-word loads).  
- [ ] **Subtask B3:** Create a functional test bench to confirm correct data alignment, sign extension, and memory interface handling.  

### Task C: Store Unit  
- [ ] **Subtask C1:** Implement store instructions (SB, SH, SW) following RISC-V alignment and data-size rules.  
- [ ] **Subtask C2:** Parameterize store width to match load-unit configurations and ensure consistent alignment checks.  
- [ ] **Subtask C3:** Test store operations with edge-case addresses and verify memory interface timing.  

### Task D: Multiplier Unit  
- [ ] **Subtask D1:** Implement a parameterized multiplication unit (e.g., iterative or pipelined multiplier) that supports MUL instructions (RV32M extension).  
- [ ] **Subtask D2:** Provide configuration options for hardware complexity vs. performance trade-offs (e.g., multi-cycle vs. single-cycle).  
- [ ] **Subtask D3:** Validate multiplier correctness over a broad range of operands, including corner cases (e.g., multiplying by zero, maximum positive/negative values).  
