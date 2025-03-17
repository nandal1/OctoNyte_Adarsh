# signoff.sdc - Synopsys Design Constraints for final timing signoff

# Same basic constraints as PNR, but you can add or refine them if needed.

create_clock -name clk -period 10.0 [get_ports clk]

set_input_delay  2.0 -clock clk [get_ports {input_*}]
set_output_delay 2.0 -clock clk [get_ports {output_*}]

# If you have any multi-cycle paths or additional constraints, add them here.

# Asynchronous reset remains a false path.
set_false_path -from [get_ports rst_n]

