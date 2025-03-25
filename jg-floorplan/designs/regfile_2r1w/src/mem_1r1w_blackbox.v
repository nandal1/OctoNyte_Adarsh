module mem_1r1w (
    input  wire        clk,
    input  wire [3:0]  read_addr,
    input  wire        read,
    output wire [31:0] read_data,
    input  wire [3:0]  write_addr,
    input  wire        write,
    input  wire [31:0] write_data,

    input  wire        VPWR,  // <-- Declare this
    input  wire        VGND   // <-- And this
);
endmodule
