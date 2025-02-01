# constraints/design.sdc
# This SDC file defines basic timing constraints.

# Define a clock on the port "clock" with a period of 10 ns.
create_clock -name clock -period 10 [get_ports clock]