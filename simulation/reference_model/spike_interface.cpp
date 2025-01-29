#include <windows.h>
#include <iostream>

#define PIPE_NAME R"(\\.\pipe\spike_rtl_pipe)"

void send_instruction(const std::string &instruction)
{
    HANDLE hPipe = CreateFileA(
        PIPE_NAME, GENERIC_WRITE, 0, NULL, OPEN_EXISTING, 0, NULL);

    if (hPipe == INVALID_HANDLE_VALUE)
    {
        std::cerr << "Error: Unable to open pipe. Make sure the receiver is running." << std::endl;
        return;
    }

    DWORD bytesWritten;
    WriteFile(hPipe, instruction.c_str(), instruction.size(), &bytesWritten, NULL);
    CloseHandle(hPipe);
}

int main()
{
    std::string instruction = "ADD x1, x2, x3";
    send_instruction(instruction);
    std::cout << "Sent instruction to RTL: " << instruction << std::endl;
    return 0;
}
