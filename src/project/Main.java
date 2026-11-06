package project;

public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.addISA(Instruction.M6800_ISA);
        cpu.mem.attach(
                project.mem.ROM.from_ints(0, new int[]{
                        0x01,
                        0x02,
                        -1,
                        0x03,
                        0x7F
                })
        );
        cpu.stepUntilStop();
//        System.out.println((byte)(
//                (byte)0b1100_0000
//        ));
    }
}