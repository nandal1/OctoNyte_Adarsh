# Single Issue OctoNyte Pipeline

|       | 0    | 1       | 2     | 3     | 4    | 5    | 6    | 7    | 8    |
| ----- | ---- | ------- | ----- | ----- | ---- | ---- | ---- | ---- | ---- |
| Ld/St | Dec  | RF Read | Agen  | Mem1  | WB   |      |      |      |      |
| ALU   | Dec  | RF Read | Exec1 | Exec2 | WB   |      |      |      |      |



Now let's consider the case of single-issue with 4 threads.

|      | 0    | 1       | 2       | 3       | 4       | 5       | 6       | 7       | 8       | 9    | 10   | 11   |
| ---- | ---- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ---- | ---- | ---- |
| T1   | Dec  | RF Read | Agen    | Mem     | WB      |         |         |         |         |      |      |      |
| T2   |      | Dec     | RF Read | Agen    | Mem     | WB      |         |         |         |      |      |      |
| T3   |      |         | Dec     | RF Read | Agen    | Mem     | WB      |         |         |      |      |      |
| T4   |      |         |         | Dec     | RF Read | Agen    | Mem     | WB      |         |      |      |      |
| T1   |      |         |         |         | Dec     | RF Read | Agen    | Mem     | WB      |      |      |      |
| T2   |      |         |         |         |         | Dec     | RF Read | Agen    | Mem     | WB   |      |      |
| T3   |      |         |         |         |         |         | Dec     | RF Read | Agen    | Mem1 | WB   |      |
| T4   |      |         |         |         |         |         |         | Dec     | RF Read | Agen | Mem1 | WB   |

With this pipeline and 4 threads, the second T1 instruction that issues in clock cycle 4 completes its WB stage before the RF read. Further, after the pipeline fills, each execution unit is completely utilized without structural hazards.



The pipeline per thread appears as:

|       | 0    | 1       | 2    | 3    | 4    | 5       | 6     | 7     | 8    | 9       | 10    | 11    | 12   |
| ----- | ---- | ------- | ---- | ---- | ---- | ------- | ----- | ----- | ---- | ------- | ----- | ----- | ---- |
| Ld/St | Dec  | RF Read | Agen | Mem  | WB   |         |       |       |      |         |       |       |      |
| ALU   |      |         |      |      | Dec  | RF Read | Exec1 | Exec2 | WB   |         |       |       |      |
| ALU   |      |         |      |      |      |         |       |       | Dec  | RF Read | Exec1 | Exec2 | WB   |





# Vector Multiple Issue OctoNyte Pipeline

Due to the long latency of accessing Vector memory, an 8-threaded design allows cache bypassing with additional cycles for accessing memory.

|       | 0    | 1        | 2     | 3     | 4       | 5    | 6    | 7    | 8    |
| ----- | ---- | -------- | ----- | ----- | ------- | ---- | ---- | ---- | ---- |
| Ld/St | Dec  | RF Read  | Agen  | XFer  | Int Ext | Mem0 | Mem1 | Mem2 | WB   |
| ALU   | Dec  | RF Read  | Exec1 | Exec2 | XFer    | WB   |      |      |      |
| I_Mul | Dec  | RF Read  | Exec1 | Exec2 | Exec3   | XFer | WB   |      |      |
| V_Mul | Dec  | VRF Read | Mul1  | Mul2  | Add1    | Add2 | XFer | WB   |      |



# Instruction Issue

Assuming instructions have already been fetched and hazards have been resolved, any of the 4 instructions may issue at the same time dependent upon the number of Read ports. Writeback is staggered in time.

