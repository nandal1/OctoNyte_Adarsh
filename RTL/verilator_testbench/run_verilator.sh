#!/bin/bash
# run_verilator.sh
#
# This script automates the Verilator simulation flow for your designs.
# It supports two modes:
#  1. Batch mode (default): Process all design files in the source directory.
#  2. Single test mode: Process only the design specified by its name.
#
# Usage:
#   ./run_verilator.sh [--stop-first] [DesignName]
#
# The flag "--stop-first" will stop the simulation on the first failure.
# If a DesignName is provided (e.g. "WaveFormGenerator"), then the script will
# run only that design (assuming its Verilog file is DesignName.v in the source directory,
# and its testbench is DesignName_testbench.cpp in the current directory).
#
# Adjust directories and naming conventions as needed.

# Directories (modify as necessary)
TESTBENCH_DIR="verilator_testbench"
SOURCE_DIR="../Chisel/generators/generated/verilog_sv2v"

# Parse command-line arguments.
STOP_FIRST=""
DESIGN_NAME=""

for arg in "$@"; do
    if [ "$arg" == "--stop-first" ]; then
        STOP_FIRST="--stop-first"
        echo "Stop on first fail mode enabled."
    else
        DESIGN_NAME="$arg"
    fi
done

# Function to process a single design.
process_design() {
    local design_basename="$1"
    echo "-----------------------------------------------------"
    echo "Processing design: $design_basename"
    
    # Define paths.
    local DESIGN_FILE="${SOURCE_DIR}/${design_basename}.v"
    local TESTBENCH_CPP="${design_basename}_testbench.cpp"
    
    # Check that the design and testbench files exist.
    if [ ! -f "$DESIGN_FILE" ]; then
        echo "Error: Design file '$DESIGN_FILE' not found."
        echo "Error: Design file '$DESIGN_FILE' not found." >> "$ERROR_LOG"
        return 1
    fi

    if [ ! -f "$TESTBENCH_CPP" ]; then
        echo "Warning: Testbench file '$TESTBENCH_CPP' not found. Skipping design $design_basename."
        echo "Warning: Testbench file '$TESTBENCH_CPP' not found for design $design_basename." >> "$ERROR_LOG"
        return 1
    fi
    
    # Run Verilator.
    echo "Running Verilator on $DESIGN_FILE with testbench $TESTBENCH_CPP"
    verilator --cc "$DESIGN_FILE" --exe "$TESTBENCH_CPP" --trace
    if [ $? -ne 0 ]; then
        echo "Verilator failed on $design_basename. See error above."
        echo "Verilator failed on $design_basename" >> "$ERROR_LOG"
        return 1
    fi
    
    # Build the simulation executable. (Assumes the makefile is generated in obj_dir.)
    make -C obj_dir -f V${design_basename}.mk V${design_basename}
    if [ $? -ne 0 ]; then
        echo "Make failed for design $design_basename."
        echo "Make failed for design $design_basename" >> "$ERROR_LOG"
        return 1
    fi
    
    # Run the simulation executable.
    echo "Running simulation for $design_basename..."
    ./obj_dir/V${design_basename} $STOP_FIRST
    sim_exit_code=$?
    if [ $sim_exit_code -ne 0 ]; then
        echo "Simulation for $design_basename FAILED (exit code $sim_exit_code)."
        echo "Simulation for $design_basename FAILED" >> "$ERROR_LOG"
        # If stop-on-first mode is enabled, then exit immediately.
        if [ "$STOP_FIRST" == "--stop-first" ]; then
            echo "Stopping on first failure as requested."
            cat "$ERROR_LOG"
            rm "$ERROR_LOG"
            exit 1
        fi
        return 1
    else
        echo "Simulation for $design_basename PASSED."
    fi
    
    return 0
}

# Create a temporary file to collect error messages.
ERROR_LOG=$(mktemp)

# If a design name is provided, run only that test; otherwise, process all designs.
if [ -n "$DESIGN_NAME" ]; then
    process_design "$DESIGN_NAME"
else
    # Loop over every design file in the SOURCE_DIR with extension .v.
    for design in "$SOURCE_DIR"/*.v; do
        design_basename=$(basename "$design" .v)
        process_design "$design_basename"
    done
fi

echo "-----------------------------------------------------"
echo "Testbench run completed."

# Print a summary of all errors.
if [ -s "$ERROR_LOG" ]; then
    echo "The following errors were encountered:"
    cat "$ERROR_LOG"
    rm "$ERROR_LOG"
    exit 1
else
    echo "All design simulations passed successfully."
    rm "$ERROR_LOG"
    exit 0
fi