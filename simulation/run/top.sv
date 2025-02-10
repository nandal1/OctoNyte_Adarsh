// top.sv - Simple Example Verilog Top Module
module top(
    input logic clk,
    input logic rst,
    input logic [31:0] in_signal,
    output logic [31:0] out_signal
);
    always_ff @(posedge clk or posedge rst) begin
        if (rst)
            out_signal <= 0;
        else
            out_signal <= in_signal + 1;  // Simple Increment
    end
endmodule
