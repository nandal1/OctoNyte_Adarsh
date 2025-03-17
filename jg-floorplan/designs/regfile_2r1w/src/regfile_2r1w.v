module regfile_2r1w(
    input  wire         clk,
    input  wire         rst_n,
    input  wire         rs1_read,
    input  wire [3:0]   rs1_addr,    // Assuming DEPTH_LOG2 = 4
    output wire [31:0]  rs1_rdata,   // Assuming WIDTH = 32
    input  wire         rs2_read,
    input  wire [3:0]   rs2_addr,    // Assuming DEPTH_LOG2 = 4
    output wire [31:0]  rs2_rdata,   // Assuming WIDTH = 32
    input  wire [3:0]   rd_addr,
    input  wire [31:0]  rd_wdata,
    input  wire         rd_write
);

    // In this version, we instantiate the hardened memory macros using their default
    // parameter values (DEPTH_LOG2 = 4, WIDTH = 32). We are not overriding any parameters.
    // The literal connections are used for the write signals.

    mem_1r1w lane0 (
        .clk(clk),
        .read_addr(rs1_addr),
        .read(rs1_read),
        .read_data(rs1_rdata),
        .write_addr(rd_addr),
        .write(rd_write),
        .write_data(rd_wdata)
    );

    mem_1r1w lane1 (
        .clk(clk),
        .read_addr(rs2_addr),
        .read(rs2_read),
        .read_data(rs2_rdata),
        .write_addr(rd_addr),
        .write(rd_write),
        .write_data(rd_wdata)
    );
    
endmodule
