package project;

import project.Mask.MaskFormat;
import project.Mask.MaskGroup;

public class Example {
    public static void main(String[] args) {

        System.out.println("\n Example of how to use the CPU (using TEST_ISA)");
        CPU cpu = new CPU();
        cpu.addISA(Instruction.M6800_ISA);
        cpu.mem.attach(
                project.mem.ROM.from_ints(0, new int[]{
                        0x47,
                        0x4C,
                        0xB7,
                        0x00,
                        0x80,
                        0x48,
                        0x3E,
                        0X20,
                        0XF9
                })
        );
        for(int i=0;i<7;i++) {
            cpu.stepUntilStop();
            System.out.println(cpu.R_A);
        }



        System.out.println("\n Example of MaskGroup extracting bits");
        MaskGroup group =  new MaskFormat("AAAABBBB").of(0x45);
        System.out.println(group.Get('A')); // extracts only bits that are A in the format
        System.out.println(group.Get('B')); // extracts only bits that are B in the format
        System.out.println(group.GetCondensed('A')); // extracts only bits that are B in the format, but moves them to the right so it starts at 1.
    }
}