/// sta-blackbox
(* blackbox *)
module mem_1r1w #(
    parameter DEPTH_LOG2 = 4,
    parameter WIDTH = 32
)(
    input  wire clk,
    input  wire [DEPTH_LOG2-1:0] read_addr,
    input  wire read,
    output reg [WIDTH-1:0] read_data,
    input  wire [DEPTH_LOG2-1:0] write_addr,
    input  wire write,
    input  wire [WIDTH-1:0] write_data
);
// Blackbox: No RTL implementation provided.
endmodule

