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
                cpu.R_A = (byte)(Util.b2iu(cpu.R_A)>>>1);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x54, cpu->{ // LSR B
                cpu.set_flag(CPU.F_C, ((cpu.R_B&1)!=0));
                cpu.R_B = (byte)(Util.b2iu(cpu.R_B)>>>1);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x64, cpu->{ // LSR IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((Util.b2iu(val)&1)!=0));
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
                cpu.writeMem(addr, (byte)(Util.b2iu(val)>>>1));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),




            new Instruction(0x46, cpu->{ // ROR A
                int f = cpu.get_flag(CPU.F_C)?0x80:0;
                cpu.set_flag(CPU.F_C, ((cpu.R_A&0x80)!=0));
                cpu.R_A = (byte)((Util.b2iu(cpu.R_A)>>>1) | f);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x56, cpu->{ // ROR B
                int f = cpu.get_flag(CPU.F_C)?0x80:0;
                cpu.set_flag(CPU.F_C, ((cpu.R_B&0x80)!=0));
                cpu.R_B = (byte)((Util.b2iu(cpu.R_B)>>>1) | f);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x66, cpu->{ // ROR IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                int f = cpu.get_flag(CPU.F_C)?0x80:0;
                cpu.set_flag(CPU.F_C, ((val&0x80)!=0));
                cpu.writeMem(addr, (byte)((Util.b2iu(val)>>>1) | f));
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
                cpu.writeMem(addr, (byte)((Util.b2iu(val)>>>1) | f));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),



            new Instruction(0x47, cpu->{ // ASR A
                cpu.set_flag(CPU.F_C, ((cpu.R_A&1)!=0));
                cpu.R_A = (byte)(Util.b2iu(cpu.R_A)>>1);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x57, cpu->{ // ASR B
                cpu.set_flag(CPU.F_C, ((cpu.R_B&1)!=0));
                cpu.R_B = (byte)(Util.b2iu(cpu.R_B)>>1);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x67, cpu->{ // ASR IDX
                short addr = (short)(cpu.R_IDX + cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&1)!=0));
                cpu.writeMem(addr, (byte)(Util.b2iu(val)>>1));
                cpu.update_ZS_flags_from_output(val);
                cpu.set_flag(CPU.F_C, true);
                cpu.set_flag(CPU.F_O, cpu.get_flag(CPU.F_C)^cpu.get_flag(CPU.F_S));
                return null;
            }),
            new Instruction(0x77, cpu->{ // ASR EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte val = cpu.fetch(addr);
                cpu.set_flag(CPU.F_C, ((val&1)!=0));
                cpu.writeMem(addr, (byte)(Util.b2iu(val)>>1));
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



            new Instruction(0x86, cpu -> { // LDAA IMM
                cpu.R_A = cpu.fetchNext();
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),
            new Instruction(0x96, cpu -> { //LDAA DIR
                cpu.R_A = cpu.fetch((short)(cpu.fetchNext() & 0xFF));
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xA6, cpu -> { // LDA IDX
                cpu.R_A = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF)));
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xB6, cpu -> { //LDAA EXT
                cpu.R_A = cpu.fetch(Util.b2s(cpu.fetchNext(),cpu.fetchNext()));
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xC6, cpu -> { //LDAB IMM
                cpu.R_B = cpu.fetchNext();
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),
            new Instruction(0xD6, cpu -> { //LDAB DIR
                cpu.R_B = cpu.fetch((short) (cpu.fetchNext() & 0xFF));
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xE6, cpu -> { //LDAB IDX
                cpu.R_B = cpu.fetch((short)(cpu.R_IDX + (cpu.fetchNext() & 0xFF)));
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xF6, cpu -> { //LDAB  EXT
                cpu.R_B = cpu.fetch(Util.b2s(cpu.fetchNext(),cpu.fetchNext()));
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),


            new Instruction(0x97, cpu -> { // STAA DIR
                cpu.mem.write((short)(cpu.R_IDX + (cpu.fetchNext() & 0xFF)), cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xA7, cpu -> { //STAA IDX
                cpu.mem.write((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF)), cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xB7, cpu -> { //STAA EXT
                cpu.mem.write(Util.b2s(cpu.fetchNext(), cpu.fetchNext()), cpu.R_A);
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xD7, cpu -> { //STAB DIR
                cpu.mem.write((short) (cpu.fetchNext() & 0xFF), cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),

            new Instruction(0xE7, cpu -> { // STAB IDX
                cpu.mem.write((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF)), cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),

            new Instruction(0xF7, cpu -> { //STAB EXT
                cpu.mem.write(Util.b2s(cpu.fetchNext(), cpu.fetchNext()), cpu.R_B);
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),


            new Instruction(0x8e, cpu -> { //LDS IMM
                cpu.R_SP = Util.b2s(cpu.fetchNext(),cpu.fetchNext());
                cpu.set_flag(CPU.F_Z, cpu.R_SP == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_SP & 0x8000) != 0);
                cpu.set_flag(CPU.F_O,false);
                return null;
            }),
            new Instruction(0x9E, cpu -> { // LDS DIR
                short addr = (short) (cpu.fetchNext() & 0xFF);
                cpu.R_SP = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1)));
                cpu.set_flag(CPU.F_Z, cpu.R_SP == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_SP & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xAE, cpu -> { // LDS IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                cpu.R_SP = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1)));
                cpu.set_flag(CPU.F_Z, cpu.R_SP == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_SP & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xBE, cpu -> { // LDS EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                cpu.R_SP = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1)));
                cpu.set_flag(CPU.F_Z, cpu.R_SP == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_SP & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),

            new Instruction(0x9F, cpu -> { // STS DIR
                short addr = (short) (cpu.fetchNext() & 0xFF);
                cpu.mem.write(addr, (byte) ((cpu.R_SP >> 8) & 0xFF));
                cpu.mem.write((short) (addr + 1), (byte) (cpu.R_SP & 0xFF));
                cpu.set_flag(CPU.F_Z, cpu.R_SP == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_SP & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xAF, cpu -> { // STS IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                cpu.mem.write(addr, (byte) ((cpu.R_SP >> 8) & 0xFF));
                cpu.mem.write((short) (addr + 1), (byte) (cpu.R_SP & 0xFF));
                cpu.set_flag(CPU.F_Z, cpu.R_SP == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_SP & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xBF, cpu -> { // STS EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                cpu.mem.write(addr, (byte) ((cpu.R_SP >> 8) & 0xFF));
                cpu.mem.write((short) (addr + 1), (byte) (cpu.R_SP & 0xFF));
                cpu.set_flag(CPU.F_Z, cpu.R_SP == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_SP & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),

            new Instruction(0xCE, cpu -> { // LDX IMM
                cpu.R_IDX = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_IDX & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xDE, cpu -> { // LDX DIR
                short addr = (short) (cpu.fetchNext() & 0xFF);
                cpu.R_IDX = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1)));
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_IDX & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xEE, cpu -> { // LDX IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                cpu.R_IDX = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1)));
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_IDX & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xFE, cpu -> { // LDX EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                cpu.R_IDX = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1)));
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_IDX & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),

            new Instruction(0xDF, cpu -> { // STX DIR
                short addr = (short) (cpu.fetchNext() & 0xFF);
                cpu.mem.write(addr, (byte) ((cpu.R_IDX >> 8) & 0xFF));
                cpu.mem.write((short) (addr + 1), (byte) (cpu.R_IDX & 0xFF));
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_IDX & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xEF, cpu -> { // STX IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                cpu.mem.write(addr, (byte) ((cpu.R_IDX >> 8) & 0xFF));
                cpu.mem.write((short) (addr + 1), (byte) (cpu.R_IDX & 0xFF));
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_IDX & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0xFF, cpu -> { // STX EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                cpu.mem.write(addr, (byte) ((cpu.R_IDX >> 8) & 0xFF));
                cpu.mem.write((short) (addr + 1), (byte) (cpu.R_IDX & 0xFF));
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                cpu.set_flag(CPU.F_S, (cpu.R_IDX & 0x8000) != 0);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),

            new Instruction(0x16, cpu -> { // TAB
                cpu.R_B = cpu.R_A;
                cpu.update_ZS_flags_from_output(cpu.R_B);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0x17, cpu -> { // TBA
                cpu.R_A = cpu.R_B;
                cpu.update_ZS_flags_from_output(cpu.R_A);
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0x06, cpu -> { // TAP
                cpu.R_S = (byte) (cpu.R_A | 0b1100_0000);
                return null;
            }),
            new Instruction(0x07, cpu -> { // TPA
                cpu.R_A = cpu.R_S;
                return null;
            }),
            new Instruction(0x30, cpu -> { // TSX
                cpu.R_IDX = (short) (cpu.R_SP + 1);
                return null;
            }),
            new Instruction(0x35, cpu -> { // TXS
                cpu.R_SP = (short) (cpu.R_IDX - 1);
                return null;
            }),
            new Instruction(0x36, cpu -> { // PSHA
                cpu.push_to_stack(cpu.R_A);
                return null;
            }),
            new Instruction(0x37, cpu -> { // PSHB
                cpu.push_to_stack(cpu.R_B);
                return null;
            }),
            new Instruction(0x32, cpu -> { // PULA
                cpu.R_A = cpu.pop_from_stack();
                return null;
            }),
            new Instruction(0x33, cpu -> { // PULB
                cpu.R_B = cpu.pop_from_stack();
                return null;
            }),
            new Instruction(0x0C, cpu -> { // CLC
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x0D, cpu -> { // SEC
                cpu.set_flag(CPU.F_C, true);
                return null;
            }),
            new Instruction(0x0A, cpu -> { // CLV
                cpu.set_flag(CPU.F_O, false);
                return null;
            }),
            new Instruction(0x0B, cpu -> { // SEV
                cpu.set_flag(CPU.F_O, true);
                return null;
            }),
            new Instruction(0x0E, cpu -> { // CLI
                cpu.set_flag(CPU.F_I, false);
                return null;
            }),
            new Instruction(0x0F, cpu -> { // SEI
                cpu.set_flag(CPU.F_I, true);
                return null;
            }),
            new Instruction(0x4F, cpu -> { // CLRA
                cpu.R_A = 0;
                cpu.set_flag(CPU.F_S, false);
                cpu.set_flag(CPU.F_Z, true);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x5F, cpu -> { // CLRB
                cpu.R_B = 0;
                cpu.set_flag(CPU.F_S, false);
                cpu.set_flag(CPU.F_Z, true);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x6F, cpu -> { // CLR IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                cpu.mem.write(addr, (byte) 0);
                cpu.set_flag(CPU.F_S, false);
                cpu.set_flag(CPU.F_Z, true);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x7F, cpu -> { // CLR EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                cpu.mem.write(addr, (byte) 0);
                cpu.set_flag(CPU.F_S, false);
                cpu.set_flag(CPU.F_Z, true);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x40, cpu -> { // NEGA
                byte res = (byte) (-cpu.R_A);
                cpu.R_A = res;
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                cpu.set_flag(CPU.F_C, res != 0);
                return null;
            }),
            new Instruction(0x50, cpu -> { // NEGB
                byte res = (byte) (-cpu.R_B);
                cpu.R_B = res;
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                cpu.set_flag(CPU.F_C, res != 0);
                return null;
            }),
            new Instruction(0x60, cpu -> { // NEG IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                byte res = (byte) (-cpu.fetch(addr));
                cpu.mem.write(addr, res);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                cpu.set_flag(CPU.F_C, res != 0);
                return null;
            }),
            new Instruction(0x70, cpu -> { // NEG EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte res = (byte) (-cpu.fetch(addr));
                cpu.mem.write(addr, res);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                cpu.set_flag(CPU.F_C, res != 0);
                return null;
            }),
            new Instruction(0x4C, cpu -> { // INCA
                byte res = (byte) (cpu.R_A + 1);
                cpu.R_A = res;
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                return null;
            }),
            new Instruction(0x5C, cpu -> { // INCB
                byte res = (byte) (cpu.R_B + 1);
                cpu.R_B = res;
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                return null;
            }),
            new Instruction(0x6C, cpu -> { // INC IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                byte res = (byte) (cpu.fetch(addr) + 1);
                cpu.mem.write(addr, res);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                return null;
            }),
            new Instruction(0x7C, cpu -> { // INC EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte res = (byte) (cpu.fetch(addr) + 1);
                cpu.mem.write(addr, res);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == (byte) 0x80);
                return null;
            }),
            new Instruction(0x08, cpu -> { // INX
                cpu.R_IDX = (short) (cpu.R_IDX + 1);
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                return null;
            }),
            new Instruction(0x31, cpu -> { // INS
                cpu.R_SP = (short) (cpu.R_SP + 1);
                return null;
            }),
            new Instruction(0x4A, cpu -> { // DECA
                byte res = (byte) (cpu.R_A - 1);
                cpu.R_A = res;
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == 0x7F);
                return null;
            }),
            new Instruction(0x5A, cpu -> { // DECB
                byte res = (byte) (cpu.R_B - 1);
                cpu.R_B = res;
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == 0x7F);
                return null;
            }),
            new Instruction(0x6A, cpu -> { // DEC IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                byte res = (byte) (cpu.fetch(addr) - 1);
                cpu.mem.write(addr, res);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == 0x7F);
                return null;
            }),
            new Instruction(0x7A, cpu -> { // DEC EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                byte res = (byte) (cpu.fetch(addr) - 1);
                cpu.mem.write(addr, res);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_O, res == 0x7F);
                return null;
            }),
            new Instruction(0x09, cpu -> { // DEX
                cpu.R_IDX = (short) (cpu.R_IDX - 1);
                cpu.set_flag(CPU.F_Z, cpu.R_IDX == 0);
                return null;
            }),
            new Instruction(0x34, cpu -> { // DES
                cpu.R_SP = (short) (cpu.R_SP - 1);
                return null;
            }),
            new Instruction(0x8B, cpu -> { // ADDA IMM (R_X)
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),
            new Instruction(0x9B, cpu -> { // ADDA DIR
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),
            new Instruction(0xAB, cpu -> { // ADDA IDX
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),
            new Instruction(0xBB, cpu -> { // ADDA EXT
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),

            new Instruction(0xCB, cpu -> { // ADDB IMM (R_Y)
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),
            new Instruction(0xDB, cpu -> { // ADDB DIR
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),
            new Instruction(0xEB, cpu -> { // ADDB IDX
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),
            new Instruction(0xFB, cpu -> { // ADDB EXT
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),

            new Instruction(0x1B, cpu -> { // ABA (R_X <- R_X + R_Y)
                int a = cpu.R_A & 0xFF;
                int b = cpu.R_B & 0xFF;
                int raw = a + b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
                return null;
            }),

            new Instruction(0x89, cpu -> { // ADCA IMM
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),
            new Instruction(0x99, cpu -> { // ADCA DIR
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),
            new Instruction(0xA9, cpu -> { // ADCA IDX
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),
            new Instruction(0xB9, cpu -> { // ADCA EXT
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),

            new Instruction(0xC9, cpu -> { // ADCB IMM
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),
            new Instruction(0xD9, cpu -> { // ADCB DIR
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),
            new Instruction(0xE9, cpu -> { // ADCB IDX
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),
            new Instruction(0xF9, cpu -> { // ADCB EXT
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a + b + c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw > 0xFF);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, ((~(a ^ b) & (a ^ raw)) & 0x80) != 0);
                cpu.set_flag(CPU.F_A, ((a & 0x0F) + (b & 0x0F) + c) > 0x0F);
                return null;
            }),

            new Instruction(0x80, cpu -> { // SUBA IMM
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0x90, cpu -> { // SUBA DIR
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xA0, cpu -> { // SUBA IDX
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xB0, cpu -> { // SUBA EXT
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),

            new Instruction(0xC0, cpu -> { // SUBB IMM
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xD0, cpu -> { // SUBB DIR
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xE0, cpu -> { // SUBB IDX
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xF0, cpu -> { // SUBB EXT
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),

            new Instruction(0x10, cpu -> { // SBA (R_X <- R_X - R_Y)
                int a = cpu.R_A & 0xFF;
                int b = cpu.R_B & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),

            new Instruction(0x82, cpu -> { // SBCA IMM
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0x92, cpu -> { // SBCA DIR
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xA2, cpu -> { // SBCA IDX
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xB2, cpu -> { // SBCA EXT
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_A = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),

            new Instruction(0xC2, cpu -> { // SBCB IMM
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xD2, cpu -> { // SBCB DIR
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xE2, cpu -> { // SBCB IDX
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xF2, cpu -> { // SBCB EXT
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int c = ((cpu.R_S & CPU.F_C) != 0) ? 1 : 0;
                int raw = a - b - c;
                byte res = (byte) raw;
                cpu.R_B = res;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),

            new Instruction(0x19, cpu -> { // DAA
                int val = cpu.R_A & 0xFF;
                int lowNibble = val & 0x0F;
                int highNibble = (val >> 4) & 0x0F;
                boolean carry = (cpu.R_S & CPU.F_C) != 0;
                boolean halfCarry = (cpu.R_S & CPU.F_A) != 0;
                int correction = 0;

                if (halfCarry || lowNibble > 9) {
                    correction |= 0x06;
                }
                if (carry || highNibble > 9 || (highNibble > 8 && lowNibble > 9)) {
                    correction |= 0x60;
                    cpu.set_flag(CPU.F_C, true);
                }

                byte res = (byte) (val + correction);
                cpu.R_A = res;
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                return null;
            }),

            new Instruction(0x81, cpu -> { // CMPA IMM (R_X - M)
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0x91, cpu -> { // CMPA DIR
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xA1, cpu -> { // CMPA IDX
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xB1, cpu -> { // CMPA EXT
                int a = cpu.R_A & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),

            new Instruction(0xC1, cpu -> { // CMPB IMM
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetchNext() & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xD1, cpu -> { // CMPB DIR
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.fetchNext() & 0xFF)) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xE1, cpu -> { // CMPB IDX
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF))) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),
            new Instruction(0xF1, cpu -> { // CMPB EXT
                int a = cpu.R_B & 0xFF;
                int b = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext())) & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),

            new Instruction(0x11, cpu -> { // CBA
                int a = cpu.R_A & 0xFF;
                int b = cpu.R_B & 0xFF;
                int raw = a - b;
                byte res = (byte) raw;
                cpu.set_flag(CPU.F_C, raw < 0);
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x80) != 0);
                return null;
            }),


            new Instruction(0x8C, cpu -> { // CPX IMM
                int a = cpu.R_IDX & 0xFFFF;
                int b = Util.b2s(cpu.fetchNext(), cpu.fetchNext()) & 0xFFFF;
                int raw = a - b;
                short res = (short) raw;
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x8000) != 0);
                return null;
            }),
            new Instruction(0x9C, cpu -> { // CPX DIR
                short addr = (short) (cpu.fetchNext() & 0xFF);
                int a = cpu.R_IDX & 0xFFFF;
                int b = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1))) & 0xFFFF;
                int raw = a - b;
                short res = (short) raw;
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x8000) != 0);
                return null;
            }),
            new Instruction(0xAC, cpu -> { // CPX IDX
                short addr = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                int a = cpu.R_IDX & 0xFFFF;
                int b = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1))) & 0xFFFF;
                int raw = a - b;
                short res = (short) raw;
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x8000) != 0);
                return null;
            }),
            new Instruction(0xBC, cpu -> { // CPX EXT
                short addr = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                int a = cpu.R_IDX & 0xFFFF;
                int b = Util.b2s(cpu.fetch(addr), cpu.fetch((short) (addr + 1))) & 0xFFFF;
                int raw = a - b;
                short res = (short) raw;
                cpu.set_flag(CPU.F_Z, res == 0);
                cpu.set_flag(CPU.F_S, res < 0);
                cpu.set_flag(CPU.F_O, (((a ^ b) & (a ^ raw)) & 0x8000) != 0);
                return null;
            }),


            new Instruction(0x4D, cpu -> { // TSTA
                cpu.set_flag(CPU.F_S, cpu.R_A < 0);
                cpu.set_flag(CPU.F_Z, cpu.R_A == 0);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x5D, cpu -> { // TSTB
                cpu.set_flag(CPU.F_S, cpu.R_B < 0);
                cpu.set_flag(CPU.F_Z, cpu.R_B == 0);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x6D, cpu -> { // TST IDX
                byte val = cpu.fetch((short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF)));
                cpu.set_flag(CPU.F_S, val < 0);
                cpu.set_flag(CPU.F_Z, val == 0);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),
            new Instruction(0x7D, cpu -> { // TST EXT
                byte val = cpu.fetch(Util.b2s(cpu.fetchNext(), cpu.fetchNext()));
                cpu.set_flag(CPU.F_S, val < 0);
                cpu.set_flag(CPU.F_Z, val == 0);
                cpu.set_flag(CPU.F_O, false);
                cpu.set_flag(CPU.F_C, false);
                return null;
            }),

            new Instruction(0x20, cpu -> { // BRA
                byte offset = cpu.fetchNext();
                cpu.R_PC = (short) (cpu.R_PC + offset);
                return null;
            }),
            new Instruction(0x22, cpu -> { // BHI
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & (CPU.F_C | CPU.F_Z)) == 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x23, cpu -> { // BLS
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & (CPU.F_C | CPU.F_Z)) != 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x24, cpu -> { // BCC
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_C) == 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x25, cpu -> { // BCS
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_C) != 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x26, cpu -> { // BNE
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_Z) == 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x27, cpu -> { // BEQ
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_Z) != 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x28, cpu -> { // BVC
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_O) == 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x29, cpu -> { // BVS
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_O) != 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x2A, cpu -> { // BPL
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_S) == 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x2B, cpu -> { // BMI
                byte offset = cpu.fetchNext();
                if ((cpu.R_S & CPU.F_S) != 0) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x2C, cpu -> { // BGE
                byte offset = cpu.fetchNext();
                boolean s = (cpu.R_S & CPU.F_S) != 0;
                boolean o = (cpu.R_S & CPU.F_O) != 0;
                if (s == o) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x2D, cpu -> { // BLT
                byte offset = cpu.fetchNext();
                boolean s = (cpu.R_S & CPU.F_S) != 0;
                boolean o = (cpu.R_S & CPU.F_O) != 0;
                if (s != o) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x2E, cpu -> { // BGT
                byte offset = cpu.fetchNext();
                boolean z = (cpu.R_S & CPU.F_Z) != 0;
                boolean s = (cpu.R_S & CPU.F_S) != 0;
                boolean o = (cpu.R_S & CPU.F_O) != 0;
                if (!z && (s == o)) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),
            new Instruction(0x2F, cpu -> { // BLE
                byte offset = cpu.fetchNext();
                boolean z = (cpu.R_S & CPU.F_Z) != 0;
                boolean s = (cpu.R_S & CPU.F_S) != 0;
                boolean o = (cpu.R_S & CPU.F_O) != 0;
                if (z || (s != o)) {
                    cpu.R_PC = (short) (cpu.R_PC + offset);
                }
                return null;
            }),


            new Instruction(0x6E, cpu -> { // JMP IDX
                cpu.R_PC = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                return null;
            }),
            new Instruction(0x7E, cpu -> { // JMP EXT
                cpu.R_PC = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                return null;
            }),
            new Instruction(0xAD, cpu -> { // JSR IDX
                short target = (short) (cpu.R_IDX + (cpu.fetchNext() & 0xFF));
                cpu.push_to_stack((byte) (cpu.R_PC & 0xFF));
                cpu.push_to_stack((byte) ((cpu.R_PC >> 8) & 0xFF));
                cpu.R_PC = target;
                return null;
            }),
            new Instruction(0xBD, cpu -> { // JSR EXT
                short target = Util.b2s(cpu.fetchNext(), cpu.fetchNext());
                cpu.push_to_stack((byte) (cpu.R_PC & 0xFF));
                cpu.push_to_stack((byte) ((cpu.R_PC >> 8) & 0xFF));
                cpu.R_PC = target;
                return null;
            }),
            new Instruction(0x8D, cpu -> { // BSR
                byte offset = cpu.fetchNext();
                cpu.push_to_stack((byte) (cpu.R_PC & 0xFF));
                cpu.push_to_stack((byte) ((cpu.R_PC >> 8) & 0xFF));
                cpu.R_PC = (short) (cpu.R_PC + offset);
                return null;
            }),
            new Instruction(0x39, cpu -> { // RTS
                byte hi = cpu.pop_from_stack();
                byte lo = cpu.pop_from_stack();
                cpu.R_PC = Util.b2s(hi, lo);
                return null;
            }),


            new Instruction(0x01, cpu -> null), // NOP
            new Instruction(0x3E, cpu -> new InstructionReturn(true)), // WAI
            new Instruction(0x3F, cpu -> { // SWI
                cpu.push_to_stack((byte) (cpu.R_PC & 0xFF));
                cpu.push_to_stack((byte) ((cpu.R_PC >> 8) & 0xFF));
                cpu.push_to_stack((byte) (cpu.R_IDX & 0xFF));
                cpu.push_to_stack((byte) ((cpu.R_IDX >> 8) & 0xFF));
                cpu.push_to_stack(cpu.R_A);
                cpu.push_to_stack(cpu.R_B);
                cpu.push_to_stack(cpu.R_S);
                cpu.set_flag(CPU.F_I, true);
                cpu.R_PC = Util.b2s(cpu.fetch((short) 0xFFFA), cpu.fetch((short) 0xFFFB));
                return null;
            }),
            new Instruction(0x3B, cpu -> { // RTI
                cpu.R_S = (byte) (cpu.pop_from_stack() | 0b1100_0000);
                cpu.R_B = cpu.pop_from_stack();
                cpu.R_A = cpu.pop_from_stack();
                byte idxHi = cpu.pop_from_stack();
                byte idxLo = cpu.pop_from_stack();
                cpu.R_IDX = Util.b2s(idxHi, idxLo);
                byte pcHi = cpu.pop_from_stack();
                byte pcLo = cpu.pop_from_stack();
                cpu.R_PC = Util.b2s(pcHi, pcLo);
                return null;
            }),







    };

}

