#include <windows.h>
#include <iostream>
#include <fstream>
#include <thread>

#define PIPE_NAME R"(\\.\pipe\spike_rtl_pipe)"
#define MAX_RETRIES 5

void log_message(const std::string& msg) {
    std::ofstream log("spike_log.txt", std::ios::app);
    log << msg << std::endl;
}

void send_instruction(const std::string& instruction) {
    HANDLE hPipe;
    int retries = 0;

    // Retry logic if the pipe isn't ready
    while (retries < MAX_RETRIES) {
        hPipe = CreateFileA(PIPE_NAME, GENERIC_WRITE, 0, NULL, OPEN_EXISTING, 0, NULL);
        if (hPipe != INVALID_HANDLE_VALUE) break;  
        
        std::cerr << "Pipe not ready. Retrying (" << retries + 1 << "/" << MAX_RETRIES << ")..." << std::endl;
        log_message("Retrying pipe connection...");
        std::this_thread::sleep_for(std::chrono::seconds(1));
        retries++;
    }

    if (hPipe == INVALID_HANDLE_VALUE) {
        std::cerr << "Error: Unable to open pipe after retries." << std::endl;
        log_message("Error: Failed to open pipe.");
        return;
    }

    DWORD bytesWritten;
    WriteFile(hPipe, instruction.c_str(), instruction.size(), &bytesWritten, NULL);
    log_message("Sent instruction: " + instruction);
    CloseHandle(hPipe);
}

int main() {
    std::string instruction = "ADD x1, x2, x3";
    send_instruction(instruction);
    return 0;
}
