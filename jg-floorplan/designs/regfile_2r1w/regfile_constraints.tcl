# lane_constraints.tcl
# Constrain lane0 and lane1 to separate regions within the core area.

# Constrain lane0 to the left half of the core area.
#
# lane_constraints.tcl
# Force lane0 to a specific coordinate

set debug_file "/openlane/designs/regfile_2r1w/regfile_constraints_debug.log"
set f [open $debug_file "a"]
puts $f "lane_constraints.tcl sourced at [clock seconds]\n"
close $f

unfix_inst lane0
move_inst lane0 50 300

# Similarly for lane1, if you have another macro:
unfix_inst lane1
move_inst lane1 500 1000

#set_fp_inst_region lane0 "500 1000 1500 3000"

# Constrain lane1 to the right half of the core area.
#set_fp_inst_region lane1 "1500 1000 2500 3000"

