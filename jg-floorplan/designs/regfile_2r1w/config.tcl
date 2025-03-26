# -----------------------------------------------------------------------------
# Design Basics
# -----------------------------------------------------------------------------
set ::env(DESIGN_NAME) "regfile_2r1w"
set ::env(VERILOG_FILES) [list \
    "$::env(DESIGN_DIR)/src/regfile_2r1w.v" \
    "$::env(DESIGN_DIR)/src/mem_1r1w_blackbox.v"]
set ::env(TOP_MODULE) "regfile_2r1w"

# -----------------------------------------------------------------------------
# Timing
# -----------------------------------------------------------------------------
set ::env(CLOCK_PORT) "clk"
set ::env(CLOCK_PERIOD) 2.0

# -----------------------------------------------------------------------------
# Floorplan
# -----------------------------------------------------------------------------
set ::env(FP_SIZING) "absolute"
set ::env(DIE_AREA) "0 0 700 700"
set ::env(FP_CORE_AREA) "0 0 700 700"

# Disable ALL automatic macro placements
set ::env(RUN_BASIC_MP) 0

# Explicit DEF file for macro placements (must be FIXED)
set ::env(FP_DEF_TEMPLATE) "$::env(DESIGN_DIR)/src/manual_placement.def"

# Completely prevent all resizing/moving of macros
set ::env(PL_RESIZER_ALLOW_MACRO_MOVE) 0
set ::env(GLB_RESIZER_ALLOW_MACRO_MOVE) 0
set ::env(DPL_RESIZER_ALLOW_MACRO_MOVE) 0
set ::env(CTS_RESIZER_ALLOW_MACRO_MOVE) 0

# -----------------------------------------------------------------------------
# Hard Macro (mem_1r1w) LEF/GDS
# -----------------------------------------------------------------------------
set ::env(EXTRA_LEFS) "/openlane/designs/mem_1r1w/runs/output/results/final/lef/mem_1r1w.lef"
set ::env(EXTRA_GDS_FILES) "/openlane/designs/mem_1r1w/runs/output/results/final/gds/mem_1r1w.gds"

# -----------------------------------------------------------------------------
# Power and Ground Nets
# -----------------------------------------------------------------------------
set ::env(VDD_NETS) "vccd1"
set ::env(GND_NETS) "vssd1"
set ::env(FP_PDN_VDD_NET) "vccd1"
set ::env(FP_PDN_GND_NET) "vssd1"
set ::env(POWER_PINS) "VPWR"
set ::env(GROUND_PINS) "VGND"

# -----------------------------------------------------------------------------
# Global Net Connect & PDN (simple defaults, no custom script)
# -----------------------------------------------------------------------------
set ::env(FP_PDN_ENABLE_GLOBAL_CONNECTION) 1

# -----------------------------------------------------------------------------
# SDC Files
# -----------------------------------------------------------------------------
set ::env(PNR_SDC_FILE) "$::env(DESIGN_DIR)/src/regfile_2r1w.sdc"
set ::env(SIGNOFF_SDC_FILE) "$::env(DESIGN_DIR)/src/regfile_2r1w.sdc"
