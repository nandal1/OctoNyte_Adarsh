# global_connect.tcl
#
# Ties macros named lane0, lane1 to the top-level supply nets vccd1, vssd1.
# If your macro pins are literally named "VPWR"/"VGND", do:
globalNetConnect vccd1 -type pgpin -net {VPWR} -inst {lane0 lane1}
globalNetConnect vssd1 -type pgpin -net {VGND} -inst {lane0 lane1}
