# spm.sdc - Synopsys Design Constraints file for 500MHz operation
#
# This file sets up a 500MHz clock (period = 2.0 ns) with typical input/output delays,
# clock uncertainty, and false path constraints for asynchronous reset.
#

# Create primary clock on port "clk" with a period of 2.0 ns (500MHz)
create_clock -name clk -period 2.0 [get_ports clk]

# Set clock uncertainty to account for clock jitter (adjust as needed)
set_clock_uncertainty 0.1 [get_ports clk]

# Input delay constraints for primary input ports.
# Here, a maximum input delay of 0.3 ns is assumed. Adjust values as needed.
set_input_delay -clock clk -max 0.3 [get_ports data_in]
set_input_delay -clock clk -min 0.0 [get_ports data_in]

# Output delay constraints for primary output ports.
# Again, a maximum delay of 0.3 ns is assumed. Adjust values as needed.
set_output_delay -clock clk -max 0.3 [get_ports data_out]
set_output_delay -clock clk -min 0.0 [get_ports data_out]

# Set maximum transition (slew) constraints on I/O to ensure signal integrity
set_max_transition 0.15 [get_ports data_in]
set_max_transition 0.15 [get_ports data_out]

# Set load constraints on the input ports (example: 0.033 F). Adjust if needed.
set_load 0.033 [get_ports data_in]

# Mark asynchronous reset as a false path so it is not included in timing analysis.
set_false_path -from [get_ports rst_n] -to [all_registers]

# End of spm.sdc

