#include <windows.h>
#include <iostream>
#include <fstream>

#define PIPE_NAME R"(\\.\pipe\spike_rtl_pipe)"
#define PIPE_NAME_OUT R"(\\.\pipe\rtl_spike_pipe)"

void log_message(const std::string& msg) {
    std::ofstream log("verilator_log.txt", std::ios::app);
    log << msg << std::endl;
}

void send_result_to_spike(const std::string& result) {
    HANDLE hPipe = CreateFileA(PIPE_NAME_OUT, GENERIC_WRITE, 0, NULL, OPEN_EXISTING, 0, NULL);
    if (hPipe == INVALID_HANDLE_VALUE) {
        std::cerr << "Error: Unable to open output pipe." << std::endl;
        return;
    }

    DWORD bytesWritten;
    WriteFile(hPipe, result.c_str(), result.size(), &bytesWritten, NULL);
    CloseHandle(hPipe);
}

void execute_instruction(const std::string& instruction) {
    std::cout << "RTL executing: " << instruction << std::endl;
    log_message("Executing: " + instruction);

    std::string result = "Execution complete: " + instruction;
    send_result_to_spike(result);
}

int main() {
    HANDLE hPipe = CreateNamedPipeA(
        PIPE_NAME, PIPE_ACCESS_INBOUND, PIPE_TYPE_BYTE | PIPE_READMODE_BYTE | PIPE_WAIT,
        1, 0, 0, 0, NULL
    );

    if (hPipe == INVALID_HANDLE_VALUE) {
        std::cerr << "Error: Unable to create named pipe." << std::endl;
        return 1;
    }

    std::cout << "Waiting for instruction from Spike..." << std::endl;
    log_message("Waiting for instruction...");

    if (ConnectNamedPipe(hPipe, NULL) || GetLastError() == ERROR_PIPE_CONNECTED) {
        char buffer[256];
        DWORD bytesRead;
        if (ReadFile(hPipe, buffer, sizeof(buffer) - 1, &bytesRead, NULL)) {
            buffer[bytesRead] = '\0';
            execute_instruction(std::string(buffer));
        }
    }

    CloseHandle(hPipe);
    return 0;
}
