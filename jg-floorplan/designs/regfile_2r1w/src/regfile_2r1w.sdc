# spm.sdc - Synopsys Design Constraints file for 500MHz operation
#
# This file sets up a 500MHz clock (period = 2.0 ns) with typical input/output delays,
# clock uncertainty, and false path constraints for asynchronous reset.
#
# regfile_2r1w.sdc

create_clock -name clk -period 2.0 [get_ports clk]
set_clock_uncertainty 0.1 [get_ports clk]

# Mark the async reset as a false path if rst_n is truly asynchronous
set_false_path -from [get_ports rst_n] -to [all_registers]

# End of spm.sdc

