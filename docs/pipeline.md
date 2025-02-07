# Single Issue OctoNyte Pipeline

|       | 0    | 1      | 2    | 3      | 4    | 5    | 6    | 7    | 8    |
| ----- | ---- | ------ | ---- | ------ | ---- | ---- | ---- | ---- | ---- |
| Load  | F    | Dec+RF | Agen | Mem+WB |      |      |      |      |      |
| Store | F    | Dec+RF | Agen | Mem    |      |      |      |      |      |
| ALU   | F    | Dec+RF | Exec | WB     |      |      |      |      |      |
| BR    | F    | Dec+RF | Agen | PC     |      |      |      |      |      |



## Loads

Now let's consider the case of single-issue with 4 threads.

|      | 0    | 1      | 2      | 3      | 4      | 5      | 6      | 7      | 8      | 9      | 10     | 11   |      |      |
| ---- | ---- | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ---- | ---- | ---- |
| T0   | F    | Dec+RF | Agen   | Mem+WB |        |        |        |        |        |        |        |      |      |      |
| T1   |      | F      | Dec+RF | Agen   | Mem+WB |        |        |        |        |        |        |      |      |      |
| T2   |      |        | F      | Dec+RF | Agen   | Mem+WB |        |        |        |        |        |      |      |      |
| T3   |      |        |        | F      | Dec+RF | Agen   | Mem+WB |        |        |        |        |      |      |      |
| T0   |      |        |        |        | F      | Dec+RF | Agen   | Mem+WB |        |        |        |      |      |      |
| T1   |      |        |        |        |        | F      | Dec+RF | Agen   | Mem+WB |        |        |      |      |      |
| T2   |      |        |        |        |        |        | F      | Dec+RF | Agen   | Mem+WB |        |      |      |      |
| T3   |      |        |        |        |        |        |        | F      | Dec+RF | Agen   | Mem+WB |      |      |      |

After the pipeline fills, each execution unit is completely utilized without structural hazards.



The pipeline per thread appears as:

|      | 0    | 1      | 2    | 3      | 4    | 5      | 6    | 7      | 8    | 9      | 10   | 11     | 12   |
| ---- | ---- | ------ | ---- | ------ | ---- | ------ | ---- | ------ | ---- | ------ | ---- | ------ | ---- |
| Load | F    | Dec+RF | Agen | Mem+WB |      |        |      |        |      |        |      |        |      |
| Load |      |        |      |        | F    | Dec+RF | Agen | Mem+WB |      |        |      |        |      |
| Load |      |        |      |        |      |        |      |        | F    | Dec+RF | Agen | Mem+WB |      |



## Arbitrary Instruction Mix

|           | 0    | 1      | 2      | 3      | 4      | 5      | 6      | 7      | 8      | 9      | 10   |
| --------- | ---- | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ---- |
| T0: Load  | F    | Dec+RF | Agen   | Mem+WB |        |        |        |        |        |        |      |
| T1: ALU   |      | F      | Dec+RF | Exec   | WB     |        |        |        |        |        |      |
| T2: Load  |      |        | F      | Dec+RF | Agen   | Mem+WB |        |        |        |        |      |
| T3: BR    |      |        |        | F      | Dec+RF | Agen   | PC     |        |        |        |      |
| T0: ALU   |      |        |        |        | F      | Dec+RF | Exec   | WB     |        |        |      |
| T1: BR    |      |        |        |        |        | F      | Dec+RF | Agen   | PC     |        |      |
| T2: Load  |      |        |        |        |        |        | F      | Dec+RF | Agen   | Mem+WB |      |
| T3: Store |      |        |        |        |        |        |        | F      | Dec+RF | Agen   | Mem  |

## Thread Cycles

Each thread cycle is exactly 4 clock cycles

## Interrupts

This pipeline allows us to check for interrupts at the beginning of each thread cycle without loss of state. It also allows for up to 4 interrupts to be simultaneously processed.



# Vector Multiple Issue OctoNyte Pipeline

Due to the long latency of accessing Vector memory, an 8-threaded design allows cache bypassing with additional cycles for accessing memory.

|       | 0    | 1    | 2        | 3     | 4     | 5     | 6    | 7      | 8    | 9    |
| ----- | ---- | ---- | -------- | ----- | ----- | ----- | ---- | ------ | ---- | ---- |
| Ld/St | F    | Dec  | RF Read  | Agen  | Mem0  | Mem1  | Mem2 | WB     |      |      |
| ALU   | F    | Dec  | RF Read  | Exec1 | Exec2 | WB    |      |        |      |      |
| I_Mul | F    | Dec  | RF Read  | Exec1 | Exec2 | Exec3 | WB   |        |      |      |
| V_Mul | F    | Dec  | VRF Read | Mul1  | Mul2  | Add1  | Add2 | VRF WB |      |      |



## Load Pipeline

|      | 0    | 1    | 2       | 3       | 4       | 5       | 6       | 7       | 8       | 9       | 10      | 11      |         |         |      |      |      |      |
| ---- | ---- | ---- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- | ---- | ---- | ---- | ---- |
| T0   | F    | Dec  | RF Read | Agen    | Mem0    | Mem1    | Mem2    | WB      |         |         |         |         |         |         |      |      |      |      |
| T1   |      | F    | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0    | Mem1    | Mem2    | WB      |         |         |         |      |      |      |      |
| T2   |      |      | F       | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0    | Mem1    | Mem2    | WB      |         |         |      |      |      |      |
| T3   |      |      |         | F       | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0    | Mem1    | Mem2    | WB      |         |      |      |      |      |
| T4   |      |      |         |         | F       | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0    | Mem1    | Mem2    | WB      |      |      |      |      |
| T5   |      |      |         |         |         | F       | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0    | Mem1    | Mem2    | WB   |      |      |      |
| T6   |      |      |         |         |         |         | F       | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0    | Mem1    | Mem2 | WB   |      |      |
| T7   |      |      |         |         |         |         |         | F       | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0    | Mem1 | Mem2 | WB   |      |
| T0   |      |      |         |         |         |         |         |         | F       | Dec     | RF Read | Agen    | XFer    | Int Ext | Mem0 | Mem1 | Mem2 | WB   |

# Instruction Issue

Assuming instructions have already been fetched and hazards have been resolved, any of the 4 instructions may issue at the same time dependent upon the number of Read ports. Writeback is staggered in time.

