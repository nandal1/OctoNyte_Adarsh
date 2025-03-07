# constraints/design.sdc
# This SDC file defines basic timing constraints.

# Define a clock on the port "clock" with a period of 4 ns.
create_clock -name clock -period 4 [get_ports clock]
