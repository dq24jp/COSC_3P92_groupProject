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
                cpu.R_X = cpu.fetchNext();
                cpu.update_ZS_flags_from_output(cpu.R_X);
                return null;
            }),
            new Instruction(0x03, cpu->{
                System.out.println(Util.byte_as_hex(cpu.R_X));
                return null;
            }),
            new Instruction(0x7F, cpu->{
                System.out.println("stopping...");
                return new InstructionReturn(true);
            })
    };

    public static final Instruction[] M6800_ISA = new Instruction[]{
            // ISA goes here...
    };

}
