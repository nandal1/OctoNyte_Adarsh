import os
import subprocess
import difflib


SPIKE_PATH = "/usr/local/bin/spike"
VERILATOR_SIM_PATH = "./simv"  
TEST_BINARIES_DIR = "./test_binaries"
LOG_DIR = "./logs"

os.makedirs(LOG_DIR, exist_ok=True)

def run_spike(binary):
    spike_log = os.path.join(LOG_DIR, f"{binary}_spike.log")
    command = f"{SPIKE_PATH} --isa=rv64gc --log-commits {os.path.join(TEST_BINARIES_DIR, binary)} > {spike_log} 2>&1"
    subprocess.run(command, shell=True, check=True)
    return spike_log

def run_verilator(binary):
    verilator_log = os.path.join(LOG_DIR, f"{binary}_verilator.log")
    command = f"{VERILATOR_SIM_PATH} {os.path.join(TEST_BINARIES_DIR, binary)} > {verilator_log} 2>&1"
    subprocess.run(command, shell=True, check=True)
    return verilator_log

def compare_logs(spike_log, verilator_log):
    with open(spike_log, "r") as f1, open(verilator_log, "r") as f2:
        spike_output = f1.readlines()
        verilator_output = f2.readlines()

    diff = list(difflib.unified_diff(spike_output, verilator_output, lineterm=""))
    if diff:
        print(f"❌ Mismatch detected in {spike_log} vs {verilator_log}!")
        for line in diff[:10]:  # Show only first 10 differences
            print(line)
    else:
        print(f"✅ Logs match for {spike_log} and {verilator_log}.")

def run_tests():
    for binary in os.listdir(TEST_BINARIES_DIR):
        if binary.endswith(".elf"):  # Ensure we only process binaries
            print(f"\n🔍 Testing {binary}...")
            spike_log = run_spike(binary)
            verilator_log = run_verilator(binary)
            compare_logs(spike_log, verilator_log)

if __name__ == "__main__":
    run_tests()
