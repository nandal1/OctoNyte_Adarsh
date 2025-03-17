# pnr.sdc - Synopsys Design Constraints for Place and Route

# Create a primary clock on port "clk" with a period of 10 ns (100 MHz).
create_clock -name clk -period 10.0 [get_ports clk]

# Set input and output delays relative to this clock.
# Adjust the numeric delay values as needed for your design.
set_input_delay  2.0 -clock clk [get_ports {input_*}]
set_output_delay 2.0 -clock clk [get_ports {output_*}]

# Mark asynchronous reset as a false path so it is not included in timing analysis.
set_false_path -from [get_ports rst_n]

