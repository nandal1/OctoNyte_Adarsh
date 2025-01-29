#include <windows.h>
#include <iostream>

#define PIPE_NAME R"(\\.\pipe\spike_rtl_pipe)"

void execute_instruction(const std::string &instruction)
{
    std::cout << "RTL executing: " << instruction << std::endl;
}

int main()
{
    HANDLE hPipe = CreateNamedPipeA(
        PIPE_NAME, PIPE_ACCESS_INBOUND, PIPE_TYPE_BYTE | PIPE_READMODE_BYTE | PIPE_WAIT,
        1, 0, 0, 0, NULL);

    if (hPipe == INVALID_HANDLE_VALUE)
    {
        std::cerr << "Error: Unable to create named pipe." << std::endl;
        return 1;
    }

    std::cout << "Waiting for instruction from Spike..." << std::endl;

    if (ConnectNamedPipe(hPipe, NULL) || GetLastError() == ERROR_PIPE_CONNECTED)
    {
        char buffer[256];
        DWORD bytesRead;
        if (ReadFile(hPipe, buffer, sizeof(buffer) - 1, &bytesRead, NULL))
        {
            buffer[bytesRead] = '\0';
            execute_instruction(std::string(buffer));
        }
    }

    CloseHandle(hPipe);
    return 0;
}
