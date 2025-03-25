# Place macros at specific positions and lock them
place_macro lane0 -origin {10 10} -orient R0
place_macro lane1 -origin {400 10} -orient R0

# Lock their placement so they do not get moved
set_placement_status lane0 FIXED
set_placement_status lane1 FIXED
