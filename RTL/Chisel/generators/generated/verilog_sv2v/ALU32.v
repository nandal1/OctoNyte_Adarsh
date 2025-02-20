module ALU32 (
    input logic [31:0] A, B,
    input logic [2:0] ALUOp,
    output logic [31:0] ALUResult,
    output logic Zero
);
    always_comb begin
        case (ALUOp)
            3'b000: ALUResult = A & B;  // AND
            3'b001: ALUResult = A | B;  // OR
            3'b010: ALUResult = A + B;  // ADD
            3'b110: ALUResult = A - B;  // SUBTRACT
            3'b111: ALUResult = (A < B) ? 32'b1 : 32'b0; // SLT
            default: ALUResult = 32'b0;
        endcase
    end

    assign Zero = (ALUResult == 32'b0);
endmodule
