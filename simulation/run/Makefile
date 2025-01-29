CXX = g++
CXXFLAGS = -Wall -std=c++11

all: verilator_sim verilator_top

verilator_sim: verilator_sim.cpp
	$(CXX) $(CXXFLAGS) -o verilator_sim verilator_sim.cpp

verilator_top: verilator_top.cpp
	$(CXX) $(CXXFLAGS) -o verilator_top verilator_top.cpp

clean:
	rm -f verilator_sim verilator_top
