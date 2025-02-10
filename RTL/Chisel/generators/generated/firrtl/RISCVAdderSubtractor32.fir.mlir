module {
  hw.module @RISCVAdderSubtractor32(in %clock : i1, in %reset : i1, in %io_a : i32, in %io_b : i32, in %io_opcode : i4, in %io_carryIn : i1, out io_result : i32, out io_carryOut : i1, out io_overflowFlag : i1, out io_zeroFlag : i1, out io_negativeFlag : i1) {
    %io_a_0 = sv.wire sym @sym name "io_a" {hw.verilogName = "io_a_0"} : !hw.inout<i32>
    sv.assign %io_a_0, %io_a : i32
    %io_b_1 = sv.wire sym @sym_0 name "io_b" {hw.verilogName = "io_b_0"} : !hw.inout<i32>
    sv.assign %io_b_1, %io_b : i32
    %io_opcode_2 = sv.wire sym @sym_1 name "io_opcode" {hw.verilogName = "io_opcode_0"} : !hw.inout<i4>
    sv.assign %io_opcode_2, %io_opcode : i4
    %io_carryIn_3 = sv.wire sym @sym_2 name "io_carryIn" {hw.verilogName = "io_carryIn_0"} : !hw.inout<i1>
    sv.assign %io_carryIn_3, %io_carryIn : i1
    %c1_i33 = hw.constant 1 : i33
    %c1_i2 = hw.constant 1 : i2
    %c0_i33 = hw.constant 0 : i33
    %c0_i2 = hw.constant 0 : i2
    %true = hw.constant true
    %false = hw.constant false
    %c-2_i2 = hw.constant -2 : i2
    %c16_i6 = hw.constant 16 : i6
    %c8_i6 = hw.constant 8 : i6
    %c65535_i32 = hw.constant 65535 : i32
    %c255_i32 = hw.constant 255 : i32
    %c-32_i6 = hw.constant -32 : i6
    %_width_T_3 = sv.wire sym @sym_15 {hw.verilogName = "_width_T_3"} : !hw.inout<i6>
    sv.assign %_width_T_3, %c-32_i6 : i6
    %c-1_i32 = hw.constant -1 : i32
    %_mask_T_3 = sv.wire sym @sym_21 {hw.verilogName = "_mask_T_3"} : !hw.inout<i32>
    sv.assign %_mask_T_3, %c-1_i32 : i32
    %_io_carryOut_T_6 = sv.wire sym @sym_56 {hw.verilogName = "_io_carryOut_T_6"} : !hw.inout<i1>
    %isOverflow = sv.wire sym @sym_89 {hw.verilogName = "isOverflow"} : !hw.inout<i1>
    %isZero = sv.wire sym @sym_91 {hw.verilogName = "isZero"} : !hw.inout<i1>
    %isNegative = sv.wire sym @sym_92 {hw.verilogName = "isNegative"} : !hw.inout<i1>
    %0 = sv.read_inout %io_opcode_2 : !hw.inout<i4>
    %1 = comb.extract %0 from 3 : (i4) -> i1
    %_isSub_T = sv.wire sym @sym_8 {hw.verilogName = "_isSub_T"} : !hw.inout<i1>
    sv.assign %_isSub_T, %1 : i1
    %2 = sv.read_inout %_isSub_T : !hw.inout<i1>
    %isSub = sv.wire sym @sym_9 {hw.verilogName = "isSub"} : !hw.inout<i1>
    sv.assign %isSub, %2 : i1
    %3 = sv.read_inout %io_opcode_2 : !hw.inout<i4>
    %4 = comb.extract %3 from 2 : (i4) -> i1
    %isSigned = sv.wire sym @sym_10 {hw.verilogName = "isSigned"} : !hw.inout<i1>
    sv.assign %isSigned, %4 : i1
    %5 = sv.read_inout %io_opcode_2 : !hw.inout<i4>
    %6 = comb.extract %5 from 0 : (i4) -> i2
    %operandSize = sv.wire sym @sym_11 {hw.verilogName = "operandSize"} : !hw.inout<i2>
    sv.assign %operandSize, %6 : i2
    %7 = sv.read_inout %operandSize : !hw.inout<i2>
    %8 = comb.icmp bin eq %7, %c0_i2 : i2
    %9 = sv.wire {hw.verilogName = "_GEN"} : !hw.inout<i1>
    sv.assign %9, %8 : i1
    %_width_T = sv.wire sym @sym_12 {hw.verilogName = "_width_T"} : !hw.inout<i1>
    %10 = sv.read_inout %9 : !hw.inout<i1>
    sv.assign %_width_T, %10 : i1
    %_mask_T = sv.wire sym @sym_18 {hw.verilogName = "_mask_T"} : !hw.inout<i1>
    %11 = sv.read_inout %9 : !hw.inout<i1>
    sv.assign %_mask_T, %11 : i1
    %12 = sv.read_inout %operandSize : !hw.inout<i2>
    %13 = comb.icmp bin eq %12, %c1_i2 : i2
    %14 = sv.wire {hw.verilogName = "_GEN_0"} : !hw.inout<i1>
    sv.assign %14, %13 : i1
    %_width_T_1 = sv.wire sym @sym_13 {hw.verilogName = "_width_T_1"} : !hw.inout<i1>
    %15 = sv.read_inout %14 : !hw.inout<i1>
    sv.assign %_width_T_1, %15 : i1
    %_mask_T_1 = sv.wire sym @sym_19 {hw.verilogName = "_mask_T_1"} : !hw.inout<i1>
    %16 = sv.read_inout %14 : !hw.inout<i1>
    sv.assign %_mask_T_1, %16 : i1
    %17 = sv.read_inout %operandSize : !hw.inout<i2>
    %18 = comb.icmp bin eq %17, %c-2_i2 : i2
    %19 = sv.wire {hw.verilogName = "_GEN_1"} : !hw.inout<i1>
    sv.assign %19, %18 : i1
    %_width_T_2 = sv.wire sym @sym_14 {hw.verilogName = "_width_T_2"} : !hw.inout<i1>
    %20 = sv.read_inout %19 : !hw.inout<i1>
    sv.assign %_width_T_2, %20 : i1
    %_mask_T_2 = sv.wire sym @sym_20 {hw.verilogName = "_mask_T_2"} : !hw.inout<i1>
    %21 = sv.read_inout %19 : !hw.inout<i1>
    sv.assign %_mask_T_2, %21 : i1
    %22 = sv.read_inout %_width_T_1 : !hw.inout<i1>
    %23 = comb.mux bin %22, %c16_i6, %c-32_i6 : i6
    %_width_T_4 = sv.wire sym @sym_16 {hw.verilogName = "_width_T_4"} : !hw.inout<i6>
    sv.assign %_width_T_4, %23 : i6
    %24 = sv.read_inout %_width_T : !hw.inout<i1>
    %25 = sv.read_inout %_width_T_4 : !hw.inout<i6>
    %26 = comb.mux bin %24, %c8_i6, %25 : i6
    %width = sv.wire sym @sym_17 {hw.verilogName = "width"} : !hw.inout<i6>
    sv.assign %width, %26 : i6
    %27 = sv.read_inout %_mask_T_1 : !hw.inout<i1>
    %28 = comb.mux bin %27, %c65535_i32, %c-1_i32 : i32
    %_mask_T_4 = sv.wire sym @sym_22 {hw.verilogName = "_mask_T_4"} : !hw.inout<i32>
    sv.assign %_mask_T_4, %28 : i32
    %29 = sv.read_inout %_mask_T : !hw.inout<i1>
    %30 = sv.read_inout %_mask_T_4 : !hw.inout<i32>
    %31 = comb.mux bin %29, %c255_i32, %30 : i32
    %mask = sv.wire sym @sym_23 {hw.verilogName = "mask"} : !hw.inout<i32>
    sv.assign %mask, %31 : i32
    %32 = sv.read_inout %io_a_0 : !hw.inout<i32>
    %33 = sv.read_inout %mask : !hw.inout<i32>
    %34 = comb.and bin %32, %33 : i32
    %aEffective = sv.wire sym @sym_24 {hw.verilogName = "aEffective"} : !hw.inout<i32>
    sv.assign %aEffective, %34 : i32
    %35 = sv.read_inout %aEffective : !hw.inout<i32>
    %_fullArithmeticResult_T = sv.wire sym @sym_30 {hw.verilogName = "_fullArithmeticResult_T"} : !hw.inout<i32>
    sv.assign %_fullArithmeticResult_T, %35 : i32
    %36 = sv.read_inout %io_b_1 : !hw.inout<i32>
    %37 = sv.read_inout %mask : !hw.inout<i32>
    %38 = comb.and bin %36, %37 : i32
    %bEffective = sv.wire sym @sym_25 {hw.verilogName = "bEffective"} : !hw.inout<i32>
    sv.assign %bEffective, %38 : i32
    %39 = sv.read_inout %bEffective : !hw.inout<i32>
    %40 = comb.xor bin %39, %c-1_i32 : i32
    %_bAdjusted_T = sv.wire sym @sym_26 {hw.verilogName = "_bAdjusted_T"} : !hw.inout<i32>
    sv.assign %_bAdjusted_T, %40 : i32
    %41 = sv.read_inout %_bAdjusted_T : !hw.inout<i32>
    %42 = comb.concat %false, %41 : i1, i32
    %43 = comb.add bin %42, %c1_i33 : i33
    %_bAdjusted_T_1 = sv.wire sym @sym_27 {hw.verilogName = "_bAdjusted_T_1"} : !hw.inout<i33>
    sv.assign %_bAdjusted_T_1, %43 : i33
    %44 = sv.read_inout %_bAdjusted_T_1 : !hw.inout<i33>
    %45 = comb.extract %44 from 0 : (i33) -> i32
    %_bAdjusted_T_2 = sv.wire sym @sym_28 {hw.verilogName = "_bAdjusted_T_2"} : !hw.inout<i32>
    sv.assign %_bAdjusted_T_2, %45 : i32
    %46 = sv.read_inout %_bAdjusted_T_2 : !hw.inout<i32>
    %47 = sv.read_inout %isSub : !hw.inout<i1>
    %48 = sv.read_inout %bEffective : !hw.inout<i32>
    %49 = comb.mux bin %47, %46, %48 : i32
    %bAdjusted = sv.wire sym @sym_29 {hw.verilogName = "bAdjusted"} : !hw.inout<i32>
    sv.assign %bAdjusted, %49 : i32
    %50 = sv.read_inout %bAdjusted : !hw.inout<i32>
    %_fullArithmeticResult_T_1 = sv.wire sym @sym_31 {hw.verilogName = "_fullArithmeticResult_T_1"} : !hw.inout<i32>
    sv.assign %_fullArithmeticResult_T_1, %50 : i32
    %51 = sv.read_inout %_fullArithmeticResult_T : !hw.inout<i32>
    %52 = comb.extract %51 from 31 : (i32) -> i1
    %53 = sv.read_inout %_fullArithmeticResult_T : !hw.inout<i32>
    %54 = comb.concat %52, %53 : i1, i32
    %55 = sv.read_inout %_fullArithmeticResult_T_1 : !hw.inout<i32>
    %56 = comb.extract %55 from 31 : (i32) -> i1
    %57 = sv.read_inout %_fullArithmeticResult_T_1 : !hw.inout<i32>
    %58 = comb.concat %56, %57 : i1, i32
    %59 = comb.add bin %54, %58 : i33
    %_fullArithmeticResult_T_2 = sv.wire sym @sym_32 {hw.verilogName = "_fullArithmeticResult_T_2"} : !hw.inout<i33>
    sv.assign %_fullArithmeticResult_T_2, %59 : i33
    %60 = sv.read_inout %_fullArithmeticResult_T_2 : !hw.inout<i33>
    %_fullArithmeticResult_T_3 = sv.wire sym @sym_33 {hw.verilogName = "_fullArithmeticResult_T_3"} : !hw.inout<i33>
    sv.assign %_fullArithmeticResult_T_3, %60 : i33
    %61 = sv.read_inout %aEffective : !hw.inout<i32>
    %62 = comb.concat %false, %61 : i1, i32
    %63 = sv.read_inout %bAdjusted : !hw.inout<i32>
    %64 = comb.concat %false, %63 : i1, i32
    %65 = comb.add bin %62, %64 : i33
    %_fullArithmeticResult_T_4 = sv.wire sym @sym_34 {hw.verilogName = "_fullArithmeticResult_T_4"} : !hw.inout<i33>
    sv.assign %_fullArithmeticResult_T_4, %65 : i33
    %66 = sv.read_inout %_fullArithmeticResult_T_3 : !hw.inout<i33>
    %67 = sv.read_inout %_fullArithmeticResult_T_4 : !hw.inout<i33>
    %68 = sv.read_inout %isSigned : !hw.inout<i1>
    %69 = comb.mux bin %68, %66, %67 : i33
    %fullArithmeticResult = sv.wire sym @sym_35 {hw.verilogName = "fullArithmeticResult"} : !hw.inout<i33>
    sv.assign %fullArithmeticResult, %69 : i33
    %70 = sv.read_inout %fullArithmeticResult : !hw.inout<i33>
    %71 = comb.extract %70 from 0 : (i33) -> i32
    %72 = sv.read_inout %mask : !hw.inout<i32>
    %73 = comb.and bin %71, %72 : i32
    %74 = comb.concat %false, %73 : i1, i32
    %truncatedResult = sv.wire sym @sym_36 {hw.verilogName = "truncatedResult"} : !hw.inout<i33>
    sv.assign %truncatedResult, %74 : i33
    %75 = sv.read_inout %truncatedResult : !hw.inout<i33>
    %76 = comb.extract %75 from 0 : (i33) -> i32
    %io_result = sv.wire sym @sym_3 {hw.verilogName = "io_result_0"} : !hw.inout<i32>
    sv.assign %io_result, %76 : i32
    %77 = sv.read_inout %width : !hw.inout<i6>
    %78 = comb.icmp bin eq %77, %c8_i6 : i6
    %79 = sv.wire {hw.verilogName = "_GEN_2"} : !hw.inout<i1>
    sv.assign %79, %78 : i1
    %_isCarry_T = sv.wire sym @sym_37 {hw.verilogName = "_isCarry_T"} : !hw.inout<i1>
    %80 = sv.read_inout %79 : !hw.inout<i1>
    sv.assign %_isCarry_T, %80 : i1
    %_aSign_T = sv.wire sym @sym_60 {hw.verilogName = "_aSign_T"} : !hw.inout<i1>
    %81 = sv.read_inout %79 : !hw.inout<i1>
    sv.assign %_aSign_T, %81 : i1
    %_bSign_T = sv.wire sym @sym_69 {hw.verilogName = "_bSign_T"} : !hw.inout<i1>
    %82 = sv.read_inout %79 : !hw.inout<i1>
    sv.assign %_bSign_T, %82 : i1
    %_sumSign_T = sv.wire sym @sym_78 {hw.verilogName = "_sumSign_T"} : !hw.inout<i1>
    %83 = sv.read_inout %79 : !hw.inout<i1>
    sv.assign %_sumSign_T, %83 : i1
    %84 = sv.read_inout %fullArithmeticResult : !hw.inout<i33>
    %85 = comb.extract %84 from 8 : (i33) -> i1
    %_isCarry_T_1 = sv.wire sym @sym_38 {hw.verilogName = "_isCarry_T_1"} : !hw.inout<i1>
    sv.assign %_isCarry_T_1, %85 : i1
    %86 = sv.read_inout %width : !hw.inout<i6>
    %87 = comb.icmp bin eq %86, %c16_i6 : i6
    %88 = sv.wire {hw.verilogName = "_GEN_3"} : !hw.inout<i1>
    sv.assign %88, %87 : i1
    %_isCarry_T_2 = sv.wire sym @sym_39 {hw.verilogName = "_isCarry_T_2"} : !hw.inout<i1>
    %89 = sv.read_inout %88 : !hw.inout<i1>
    sv.assign %_isCarry_T_2, %89 : i1
    %_aSign_T_2 = sv.wire sym @sym_62 {hw.verilogName = "_aSign_T_2"} : !hw.inout<i1>
    %90 = sv.read_inout %88 : !hw.inout<i1>
    sv.assign %_aSign_T_2, %90 : i1
    %_bSign_T_2 = sv.wire sym @sym_71 {hw.verilogName = "_bSign_T_2"} : !hw.inout<i1>
    %91 = sv.read_inout %88 : !hw.inout<i1>
    sv.assign %_bSign_T_2, %91 : i1
    %_sumSign_T_2 = sv.wire sym @sym_80 {hw.verilogName = "_sumSign_T_2"} : !hw.inout<i1>
    %92 = sv.read_inout %88 : !hw.inout<i1>
    sv.assign %_sumSign_T_2, %92 : i1
    %93 = sv.read_inout %fullArithmeticResult : !hw.inout<i33>
    %94 = comb.extract %93 from 16 : (i33) -> i1
    %_isCarry_T_3 = sv.wire sym @sym_40 {hw.verilogName = "_isCarry_T_3"} : !hw.inout<i1>
    sv.assign %_isCarry_T_3, %94 : i1
    %95 = sv.read_inout %width : !hw.inout<i6>
    %96 = comb.icmp bin eq %95, %c-32_i6 : i6
    %97 = sv.wire {hw.verilogName = "_GEN_4"} : !hw.inout<i1>
    sv.assign %97, %96 : i1
    %_isCarry_T_4 = sv.wire sym @sym_41 {hw.verilogName = "_isCarry_T_4"} : !hw.inout<i1>
    %98 = sv.read_inout %97 : !hw.inout<i1>
    sv.assign %_isCarry_T_4, %98 : i1
    %_aSign_T_4 = sv.wire sym @sym_64 {hw.verilogName = "_aSign_T_4"} : !hw.inout<i1>
    %99 = sv.read_inout %97 : !hw.inout<i1>
    sv.assign %_aSign_T_4, %99 : i1
    %_bSign_T_4 = sv.wire sym @sym_73 {hw.verilogName = "_bSign_T_4"} : !hw.inout<i1>
    %100 = sv.read_inout %97 : !hw.inout<i1>
    sv.assign %_bSign_T_4, %100 : i1
    %_sumSign_T_4 = sv.wire sym @sym_82 {hw.verilogName = "_sumSign_T_4"} : !hw.inout<i1>
    %101 = sv.read_inout %97 : !hw.inout<i1>
    sv.assign %_sumSign_T_4, %101 : i1
    %102 = sv.read_inout %fullArithmeticResult : !hw.inout<i33>
    %103 = comb.extract %102 from 32 : (i33) -> i1
    %_isCarry_T_5 = sv.wire sym @sym_42 {hw.verilogName = "_isCarry_T_5"} : !hw.inout<i1>
    sv.assign %_isCarry_T_5, %103 : i1
    %104 = sv.read_inout %_isCarry_T_4 : !hw.inout<i1>
    %105 = sv.read_inout %_isCarry_T_5 : !hw.inout<i1>
    %106 = comb.and %104, %105 : i1
    %_isCarry_T_6 = sv.wire sym @sym_43 {hw.verilogName = "_isCarry_T_6"} : !hw.inout<i1>
    sv.assign %_isCarry_T_6, %106 : i1
    %107 = sv.read_inout %_isCarry_T_2 : !hw.inout<i1>
    %108 = sv.read_inout %_isCarry_T_3 : !hw.inout<i1>
    %109 = sv.read_inout %_isCarry_T_6 : !hw.inout<i1>
    %110 = comb.mux bin %107, %108, %109 : i1
    %_isCarry_T_7 = sv.wire sym @sym_44 {hw.verilogName = "_isCarry_T_7"} : !hw.inout<i1>
    sv.assign %_isCarry_T_7, %110 : i1
    %111 = sv.read_inout %_isCarry_T : !hw.inout<i1>
    %112 = sv.read_inout %_isCarry_T_1 : !hw.inout<i1>
    %113 = sv.read_inout %_isCarry_T_7 : !hw.inout<i1>
    %114 = comb.mux bin %111, %112, %113 : i1
    %isCarry = sv.wire sym @sym_45 {hw.verilogName = "isCarry"} : !hw.inout<i1>
    sv.assign %isCarry, %114 : i1
    %115 = sv.read_inout %isSigned : !hw.inout<i1>
    %116 = comb.xor bin %115, %true : i1
    %_isBorrow_T = sv.wire sym @sym_46 {hw.verilogName = "_isBorrow_T"} : !hw.inout<i1>
    sv.assign %_isBorrow_T, %116 : i1
    %117 = sv.read_inout %_isBorrow_T : !hw.inout<i1>
    %118 = sv.read_inout %isSub : !hw.inout<i1>
    %119 = comb.and bin %118, %117 : i1
    %_isBorrow_T_1 = sv.wire sym @sym_47 {hw.verilogName = "_isBorrow_T_1"} : !hw.inout<i1>
    sv.assign %_isBorrow_T_1, %119 : i1
    %120 = sv.read_inout %bEffective : !hw.inout<i32>
    %121 = sv.read_inout %aEffective : !hw.inout<i32>
    %122 = comb.icmp bin ult %121, %120 : i32
    %_isBorrow_T_2 = sv.wire sym @sym_48 {hw.verilogName = "_isBorrow_T_2"} : !hw.inout<i1>
    sv.assign %_isBorrow_T_2, %122 : i1
    %123 = sv.read_inout %_isBorrow_T_1 : !hw.inout<i1>
    %124 = sv.read_inout %_isBorrow_T_2 : !hw.inout<i1>
    %125 = comb.and %123, %124 : i1
    %isBorrow = sv.wire sym @sym_49 {hw.verilogName = "isBorrow"} : !hw.inout<i1>
    sv.assign %isBorrow, %125 : i1
    %126 = sv.read_inout %isSigned : !hw.inout<i1>
    %127 = comb.xor bin %126, %true : i1
    %_io_carryOut_T = sv.wire sym @sym_50 {hw.verilogName = "_io_carryOut_T"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T, %127 : i1
    %128 = sv.read_inout %isSub : !hw.inout<i1>
    %129 = comb.xor bin %128, %true : i1
    %_io_carryOut_T_1 = sv.wire sym @sym_51 {hw.verilogName = "_io_carryOut_T_1"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T_1, %129 : i1
    %130 = sv.read_inout %_io_carryOut_T : !hw.inout<i1>
    %131 = sv.read_inout %_io_carryOut_T_1 : !hw.inout<i1>
    %132 = comb.and bin %130, %131 : i1
    %_io_carryOut_T_2 = sv.wire sym @sym_52 {hw.verilogName = "_io_carryOut_T_2"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T_2, %132 : i1
    %133 = sv.read_inout %isSigned : !hw.inout<i1>
    %134 = comb.xor bin %133, %true : i1
    %_io_carryOut_T_3 = sv.wire sym @sym_53 {hw.verilogName = "_io_carryOut_T_3"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T_3, %134 : i1
    %135 = sv.read_inout %isSub : !hw.inout<i1>
    %136 = sv.read_inout %_io_carryOut_T_3 : !hw.inout<i1>
    %137 = comb.and bin %136, %135 : i1
    %_io_carryOut_T_4 = sv.wire sym @sym_54 {hw.verilogName = "_io_carryOut_T_4"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T_4, %137 : i1
    %138 = sv.read_inout %isBorrow : !hw.inout<i1>
    %139 = sv.read_inout %_io_carryOut_T_4 : !hw.inout<i1>
    %140 = comb.and %139, %138 : i1
    %_io_carryOut_T_5 = sv.wire sym @sym_55 {hw.verilogName = "_io_carryOut_T_5"} : !hw.inout<i1>
    sv.assign %_io_carryOut_T_5, %140 : i1
    %141 = sv.read_inout %isCarry : !hw.inout<i1>
    %142 = sv.read_inout %_io_carryOut_T_2 : !hw.inout<i1>
    %143 = sv.read_inout %_io_carryOut_T_5 : !hw.inout<i1>
    %144 = comb.mux bin %142, %141, %143 : i1
    sv.assign %_io_carryOut_T_6, %144 : i1
    %145 = sv.read_inout %_io_carryOut_T_6 : !hw.inout<i1>
    %io_carryOut = sv.wire sym @sym_4 {hw.verilogName = "io_carryOut_0"} : !hw.inout<i1>
    sv.assign %io_carryOut, %145 : i1
    %_aSign_T_8 = sv.wire sym @sym_68 {hw.verilogName = "_aSign_T_8"} : !hw.inout<i1>
    %_bSign_T_8 = sv.wire sym @sym_77 {hw.verilogName = "_bSign_T_8"} : !hw.inout<i1>
    %_sumSign_T_8 = sv.wire sym @sym_86 {hw.verilogName = "_sumSign_T_8"} : !hw.inout<i1>
    %146 = sv.read_inout %aEffective : !hw.inout<i32>
    %147 = comb.extract %146 from 7 : (i32) -> i1
    %_aSign_T_1 = sv.wire sym @sym_61 {hw.verilogName = "_aSign_T_1"} : !hw.inout<i1>
    sv.assign %_aSign_T_1, %147 : i1
    %148 = sv.read_inout %aEffective : !hw.inout<i32>
    %149 = comb.extract %148 from 15 : (i32) -> i1
    %_aSign_T_3 = sv.wire sym @sym_63 {hw.verilogName = "_aSign_T_3"} : !hw.inout<i1>
    sv.assign %_aSign_T_3, %149 : i1
    %150 = sv.read_inout %aEffective : !hw.inout<i32>
    %151 = comb.extract %150 from 31 : (i32) -> i1
    %_aSign_T_5 = sv.wire sym @sym_65 {hw.verilogName = "_aSign_T_5"} : !hw.inout<i1>
    sv.assign %_aSign_T_5, %151 : i1
    %152 = sv.read_inout %_aSign_T_4 : !hw.inout<i1>
    %153 = sv.read_inout %_aSign_T_5 : !hw.inout<i1>
    %154 = comb.and %152, %153 : i1
    %_aSign_T_6 = sv.wire sym @sym_66 {hw.verilogName = "_aSign_T_6"} : !hw.inout<i1>
    sv.assign %_aSign_T_6, %154 : i1
    %155 = sv.read_inout %_aSign_T_2 : !hw.inout<i1>
    %156 = sv.read_inout %_aSign_T_3 : !hw.inout<i1>
    %157 = sv.read_inout %_aSign_T_6 : !hw.inout<i1>
    %158 = comb.mux bin %155, %156, %157 : i1
    %_aSign_T_7 = sv.wire sym @sym_67 {hw.verilogName = "_aSign_T_7"} : !hw.inout<i1>
    sv.assign %_aSign_T_7, %158 : i1
    %159 = sv.read_inout %_aSign_T : !hw.inout<i1>
    %160 = sv.read_inout %_aSign_T_1 : !hw.inout<i1>
    %161 = sv.read_inout %_aSign_T_7 : !hw.inout<i1>
    %162 = comb.mux bin %159, %160, %161 : i1
    sv.assign %_aSign_T_8, %162 : i1
    %163 = sv.read_inout %_aSign_T_8 : !hw.inout<i1>
    %aSign = sv.wire sym @sym_57 {hw.verilogName = "aSign"} : !hw.inout<i1>
    sv.assign %aSign, %163 : i1
    %164 = sv.read_inout %bAdjusted : !hw.inout<i32>
    %165 = comb.extract %164 from 7 : (i32) -> i1
    %_bSign_T_1 = sv.wire sym @sym_70 {hw.verilogName = "_bSign_T_1"} : !hw.inout<i1>
    sv.assign %_bSign_T_1, %165 : i1
    %166 = sv.read_inout %bAdjusted : !hw.inout<i32>
    %167 = comb.extract %166 from 15 : (i32) -> i1
    %_bSign_T_3 = sv.wire sym @sym_72 {hw.verilogName = "_bSign_T_3"} : !hw.inout<i1>
    sv.assign %_bSign_T_3, %167 : i1
    %168 = sv.read_inout %bAdjusted : !hw.inout<i32>
    %169 = comb.extract %168 from 31 : (i32) -> i1
    %_bSign_T_5 = sv.wire sym @sym_74 {hw.verilogName = "_bSign_T_5"} : !hw.inout<i1>
    sv.assign %_bSign_T_5, %169 : i1
    %170 = sv.read_inout %_bSign_T_4 : !hw.inout<i1>
    %171 = sv.read_inout %_bSign_T_5 : !hw.inout<i1>
    %172 = comb.and %170, %171 : i1
    %_bSign_T_6 = sv.wire sym @sym_75 {hw.verilogName = "_bSign_T_6"} : !hw.inout<i1>
    sv.assign %_bSign_T_6, %172 : i1
    %173 = sv.read_inout %_bSign_T_2 : !hw.inout<i1>
    %174 = sv.read_inout %_bSign_T_3 : !hw.inout<i1>
    %175 = sv.read_inout %_bSign_T_6 : !hw.inout<i1>
    %176 = comb.mux bin %173, %174, %175 : i1
    %_bSign_T_7 = sv.wire sym @sym_76 {hw.verilogName = "_bSign_T_7"} : !hw.inout<i1>
    sv.assign %_bSign_T_7, %176 : i1
    %177 = sv.read_inout %_bSign_T : !hw.inout<i1>
    %178 = sv.read_inout %_bSign_T_1 : !hw.inout<i1>
    %179 = sv.read_inout %_bSign_T_7 : !hw.inout<i1>
    %180 = comb.mux bin %177, %178, %179 : i1
    sv.assign %_bSign_T_8, %180 : i1
    %181 = sv.read_inout %_bSign_T_8 : !hw.inout<i1>
    %bSign = sv.wire sym @sym_58 {hw.verilogName = "bSign"} : !hw.inout<i1>
    sv.assign %bSign, %181 : i1
    %182 = sv.read_inout %fullArithmeticResult : !hw.inout<i33>
    %183 = comb.extract %182 from 7 : (i33) -> i1
    %_sumSign_T_1 = sv.wire sym @sym_79 {hw.verilogName = "_sumSign_T_1"} : !hw.inout<i1>
    sv.assign %_sumSign_T_1, %183 : i1
    %184 = sv.read_inout %fullArithmeticResult : !hw.inout<i33>
    %185 = comb.extract %184 from 15 : (i33) -> i1
    %_sumSign_T_3 = sv.wire sym @sym_81 {hw.verilogName = "_sumSign_T_3"} : !hw.inout<i1>
    sv.assign %_sumSign_T_3, %185 : i1
    %186 = sv.read_inout %fullArithmeticResult : !hw.inout<i33>
    %187 = comb.extract %186 from 31 : (i33) -> i1
    %_sumSign_T_5 = sv.wire sym @sym_83 {hw.verilogName = "_sumSign_T_5"} : !hw.inout<i1>
    sv.assign %_sumSign_T_5, %187 : i1
    %188 = sv.read_inout %_sumSign_T_4 : !hw.inout<i1>
    %189 = sv.read_inout %_sumSign_T_5 : !hw.inout<i1>
    %190 = comb.and %188, %189 : i1
    %_sumSign_T_6 = sv.wire sym @sym_84 {hw.verilogName = "_sumSign_T_6"} : !hw.inout<i1>
    sv.assign %_sumSign_T_6, %190 : i1
    %191 = sv.read_inout %_sumSign_T_2 : !hw.inout<i1>
    %192 = sv.read_inout %_sumSign_T_3 : !hw.inout<i1>
    %193 = sv.read_inout %_sumSign_T_6 : !hw.inout<i1>
    %194 = comb.mux bin %191, %192, %193 : i1
    %_sumSign_T_7 = sv.wire sym @sym_85 {hw.verilogName = "_sumSign_T_7"} : !hw.inout<i1>
    sv.assign %_sumSign_T_7, %194 : i1
    %195 = sv.read_inout %_sumSign_T : !hw.inout<i1>
    %196 = sv.read_inout %_sumSign_T_1 : !hw.inout<i1>
    %197 = sv.read_inout %_sumSign_T_7 : !hw.inout<i1>
    %198 = comb.mux bin %195, %196, %197 : i1
    sv.assign %_sumSign_T_8, %198 : i1
    %199 = sv.read_inout %_sumSign_T_8 : !hw.inout<i1>
    %sumSign = sv.wire sym @sym_59 {hw.verilogName = "sumSign"} : !hw.inout<i1>
    sv.assign %sumSign, %199 : i1
    %200 = sv.read_inout %bSign : !hw.inout<i1>
    %201 = sv.read_inout %aSign : !hw.inout<i1>
    %202 = comb.icmp bin eq %201, %200 : i1
    %_isOverflow_T = sv.wire sym @sym_87 {hw.verilogName = "_isOverflow_T"} : !hw.inout<i1>
    sv.assign %_isOverflow_T, %202 : i1
    %203 = sv.read_inout %aSign : !hw.inout<i1>
    %204 = sv.read_inout %sumSign : !hw.inout<i1>
    %205 = comb.icmp bin ne %203, %204 : i1
    %_isOverflow_T_1 = sv.wire sym @sym_88 {hw.verilogName = "_isOverflow_T_1"} : !hw.inout<i1>
    sv.assign %_isOverflow_T_1, %205 : i1
    %206 = sv.read_inout %_isOverflow_T : !hw.inout<i1>
    %207 = sv.read_inout %_isOverflow_T_1 : !hw.inout<i1>
    %208 = comb.and bin %206, %207 : i1
    sv.assign %isOverflow, %208 : i1
    %209 = sv.read_inout %isOverflow : !hw.inout<i1>
    %io_overflowFlag = sv.wire sym @sym_5 {hw.verilogName = "io_overflowFlag_0"} : !hw.inout<i1>
    sv.assign %io_overflowFlag, %209 : i1
    %210 = sv.read_inout %truncatedResult : !hw.inout<i33>
    %211 = comb.icmp bin ne %210, %c0_i33 : i33
    %_isZero_T = sv.wire sym @sym_90 {hw.verilogName = "_isZero_T"} : !hw.inout<i1>
    sv.assign %_isZero_T, %211 : i1
    %212 = sv.read_inout %_isZero_T : !hw.inout<i1>
    %213 = comb.xor bin %212, %true : i1
    sv.assign %isZero, %213 : i1
    %214 = sv.read_inout %isZero : !hw.inout<i1>
    %io_zeroFlag = sv.wire sym @sym_6 {hw.verilogName = "io_zeroFlag_0"} : !hw.inout<i1>
    sv.assign %io_zeroFlag, %214 : i1
    %215 = sv.read_inout %sumSign : !hw.inout<i1>
    %216 = sv.read_inout %isSigned : !hw.inout<i1>
    %217 = comb.and %216, %215 : i1
    sv.assign %isNegative, %217 : i1
    %218 = sv.read_inout %isNegative : !hw.inout<i1>
    %io_negativeFlag = sv.wire sym @sym_7 {hw.verilogName = "io_negativeFlag_0"} : !hw.inout<i1>
    sv.assign %io_negativeFlag, %218 : i1
    %219 = sv.read_inout %io_result : !hw.inout<i32>
    %220 = sv.read_inout %io_carryOut : !hw.inout<i1>
    %221 = sv.read_inout %io_overflowFlag : !hw.inout<i1>
    %222 = sv.read_inout %io_zeroFlag : !hw.inout<i1>
    %223 = sv.read_inout %io_negativeFlag : !hw.inout<i1>
    hw.output %219, %220, %221, %222, %223 : i32, i1, i1, i1, i1
  }
  om.class @RISCVAdderSubtractor32_Class(%basepath: !om.frozenbasepath) {
    om.class.fields
  }
}
