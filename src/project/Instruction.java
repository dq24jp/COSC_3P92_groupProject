package project;


import java.util.function.Function;


/// class containing all the relevant data for an instruction
///
/// USAGE:
/// opcode should be the relevant opcode to trigger this instruction.
/// execfunction should be the function or lambda to trigger on instruction execution. Should return either an InstructionReturn or null
///
/// EXAMPLE (null instruction)
/// ()->{return null}
public record Instruction(byte opcode, Function<CPU, InstructionReturn> execfunction) {

    public Instruction(int opcode_int, Function<CPU, InstructionReturn> execfunction){ // opcode casted to byte
        this((byte)opcode_int, execfunction);
    }

    public static final Instruction[] TEST_ISA = new Instruction[]{
            new Instruction(0x01, cpu->{
                System.out.println("test");
                return null;
            }),
            new Instruction(0x02, cpu->{
                cpu.R_A = cpu.fetchNext();
                cpu.update_ZS_flags_from_output(cpu.R_A);
                return null;
            }),
            new Instruction(0x03, cpu->{
                System.out.println(Util.byte_as_hex(cpu.R_A));
                return null;
            }),
            new Instruction(0x7F, cpu->{
                System.out.println("stopping...");
                return new InstructionReturn(true);
            })
    };

    public static final Instruction[] M6800_ISA = new Instruction[]{
            // ISA goes here...



// BITWISE OPERATORS
            new Instruction(0x43, cpu->{ // COM A
                cpu.R_A = (byte)(~cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, true);
                return null;
            }),
            new Instruction(0x53, cpu->{ // COM B
                cpu.R_B = (byte)(~cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, true);
                return null;
            }),
            new Instruction(0x63, cpu->{ // COM IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.writeMem(addr, (byte)~val);
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, true);
                return null;
            }),
            new Instruction(0x73, cpu->{ // COM IDX
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.writeMem(addr, (byte)~val);
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, true);
                return null;
            }),


            new Instruction(0x44, cpu->{ // LSR A
                cpu.set_flag(CPU.F_C, ((cpu.R_A&1)!=0));
                cpu.R_A = (byte)(cpu.R_A>>>1);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x54, cpu->{ // LSR B
                cpu.set_flag(CPU.F_C, ((cpu.R_B&1)!=0));
                cpu.R_B = (byte)(cpu.R_B>>>1);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x64, cpu->{ // LSR IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&1)!=0));
                cpu.writeMem(addr, (byte)(val>>>1));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x74, cpu->{ // LSR EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&1)!=0));
                cpu.writeMem(addr, (byte)(val>>>1));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),




            new Instruction(0x46, cpu->{ // ROR A
                int f = cpu.get_flag(CPU.F_C)?0x80:0;
                cpu.set_flag(CPU.F_C, ((cpu.R_A&0x80)!=0));
                cpu.R_A = (byte)((cpu.R_A>>>1) | f);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x56, cpu->{ // ROR B
                int f = cpu.get_flag(CPU.F_C)?0x80:0;
                cpu.set_flag(CPU.F_C, ((cpu.R_B&0x80)!=0));
                cpu.R_B = (byte)((cpu.R_B>>>1) | f);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x66, cpu->{ // ROR IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                int f = cpu.get_flag(CPU.F_C)?0x80:0;
                cpu.set_flag(CPU.F_C, ((val&0x80)!=0));
                cpu.writeMem(addr, (byte)((val>>>1) | f));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x76, cpu->{ // ROR EXT
                int f = cpu.get_flag(CPU.F_C)?0x80:0;
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&0x80)!=0));
                cpu.writeMem(addr, (byte)((val>>>1) | f));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),



            new Instruction(0x47, cpu->{ // ASR A
                cpu.set_flag(CPU.F_C, ((cpu.R_A&1)!=0));
                cpu.R_A = (byte)(cpu.R_A>>1);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x57, cpu->{ // ASR B
                cpu.set_flag(CPU.F_C, ((cpu.R_B&1)!=0));
                cpu.R_B = (byte)(cpu.R_B>>1);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x67, cpu->{ // ASR IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&1)!=0));
                cpu.writeMem(addr, (byte)(val>>1));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x77, cpu->{ // ASR EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&1)!=0));
                cpu.writeMem(addr, (byte)(val>>1));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),


            new Instruction(0x48, cpu->{ // ASL A
                cpu.set_flag(CPU.F_C, ((cpu.R_A&0x80)!=0)); 
                cpu.R_A = (byte)(cpu.R_A*2);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x58, cpu->{ // ASL B
                cpu.set_flag(CPU.F_C, ((cpu.R_B&0x80)!=0)); 
                cpu.R_B = (byte)(cpu.R_B*2);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x68, cpu->{ // ASL IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&0x80)!=0)); 
                cpu.writeMem(addr, (byte)(val*2));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x78, cpu->{ // ASL EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&0x80)!=0)); 
                cpu.writeMem(addr, (byte)(val*2));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),



            new Instruction(0x49, cpu->{ // ROL A
                int f = cpu.get_flag(CPU.F_C)?1:0;
                cpu.set_flag(CPU.F_C, ((cpu.R_A&0x80)!=0));
                cpu.R_A = (byte)((cpu.R_A*2) | f);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x59, cpu->{ // ROL B
                int f = cpu.get_flag(CPU.F_C)?1:0;
                cpu.set_flag(CPU.F_C, ((cpu.R_B&0x80)!=0));
                cpu.R_B = (byte)((cpu.R_B*2) | f);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x69, cpu->{ // ROL IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                int f = cpu.get_flag(CPU.F_C)?1:0;
                cpu.set_flag(CPU.F_C, ((val&0x80)!=0));
                cpu.writeMem(addr, (byte)((val*2) | f));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x79, cpu->{ // ROL EXT
                int f = cpu.get_flag(CPU.F_C)?1:0;
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&0x80)!=0));
                cpu.writeMem(addr, (byte)((val*2) | f));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),



            new Instruction(0x84, cpu->{ // AND A IMM
                cpu.R_A = (byte) (cpu.fetchNext()&cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0x94, cpu->{ // AND A DIR
                cpu.R_A = (byte) (cpu.fetch(cpu.fetchNext())&cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xA4, cpu->{ // AND A IDX
                cpu.R_A = (byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))&cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xB4, cpu->{ // AND A IDX
                cpu.R_A = (byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))&cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),


            new Instruction(0xC4, cpu->{ // AND B IMM
                cpu.R_B = (byte) (cpu.fetchNext()&cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xD4, cpu->{ // AND B DIR
                cpu.R_B = (byte) (cpu.fetch(cpu.fetchNext())&cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xE4, cpu->{ // AND B IDX
                cpu.R_B = (byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))&cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xF4, cpu->{ // AND B IDX
                cpu.R_B = (byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))&cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),





            new Instruction(0x85, cpu->{ // BIT A IMM
                cpu.update_ZS_flags_from_output((byte) (cpu.fetchNext()&cpu.R_A));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0x95, cpu->{ // BIT A DIR
                cpu.update_ZS_flags_from_output((byte) (cpu.fetch(cpu.fetchNext())&cpu.R_A));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xA5, cpu->{ // BIT A IDX
                cpu.update_ZS_flags_from_output((byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))&cpu.R_A));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xB5, cpu->{ // BIT A IDX
                cpu.update_ZS_flags_from_output((byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))&cpu.R_A));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),


            new Instruction(0xC5, cpu->{ // BIT B IMM
                cpu.update_ZS_flags_from_output((byte) (cpu.fetchNext()&cpu.R_B));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xD5, cpu->{ // BIT B DIR
                cpu.update_ZS_flags_from_output((byte) (cpu.fetch(cpu.fetchNext())&cpu.R_B));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xE5, cpu->{ // BIT B IDX
                cpu.update_ZS_flags_from_output((byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))&cpu.R_B));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xF5, cpu->{ // BIT B IDX
                cpu.update_ZS_flags_from_output((byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))&cpu.R_B));
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),




            new Instruction(0x88, cpu->{ // EOR A IMM
                cpu.R_A = (byte) (cpu.fetchNext()^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0x98, cpu->{ // EOR A DIR
                cpu.R_A = (byte) (cpu.fetch(cpu.fetchNext())^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xA8, cpu->{ // EOR A IDX
                cpu.R_A = (byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xB8, cpu->{ // EOR A IDX
                cpu.R_A = (byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),


            new Instruction(0xC8, cpu->{ // EOR B IMM
                cpu.R_B = (byte) (cpu.fetchNext()^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xD8, cpu->{ // EOR B DIR
                cpu.R_B = (byte) (cpu.fetch(cpu.fetchNext())^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xE8, cpu->{ // EOR B IDX
                cpu.R_B = (byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xF8, cpu->{ // EOR B IDX
                cpu.R_B = (byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),




            new Instruction(0x8A, cpu->{ // OR A IMM
                cpu.R_A = (byte) (cpu.fetchNext()^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0x9A, cpu->{ // OR A DIR
                cpu.R_A = (byte) (cpu.fetch(cpu.fetchNext())^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xAA, cpu->{ // OR A IDX
                cpu.R_A = (byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xBA, cpu->{ // OR A IDX
                cpu.R_A = (byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))^cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),


            new Instruction(0xCA, cpu->{ // OR B IMM
                cpu.R_B = (byte) (cpu.fetchNext()^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xDA, cpu->{ // OR B DIR
                cpu.R_B = (byte) (cpu.fetch(cpu.fetchNext())^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xEA, cpu->{ // OR B IDX
                cpu.R_B = (byte) (cpu.fetch((short)(cpu.R_IDX + cpu.fetchNext()))^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xFA, cpu->{ // OR B IDX
                cpu.R_B = (byte) (cpu.fetch(Util.bytes_as_short(cpu.fetchNext(), cpu.fetchNext()))^cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),






    };

}

