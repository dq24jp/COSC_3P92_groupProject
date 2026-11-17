package project;

import project.mem.MemorySet;

import java.io.IOException;
import java.util.Scanner;

public class UI {

    boolean running = true;
    CPU cpu;

    public UI(){
        this.reset();
    }

    void reset(){
        this.cpu = new CPU();

        cpu.addISA(Instruction.M6800_ISA);
    }

    public void parseCommand(String command){
        command = command.trim();

        String[] words = command.split(" ");
        String firstWord = words[0].toUpperCase();
        String afterFirstWord = command.substring(command.indexOf(" ")+1).trim();

        switch(firstWord){
            case "STOP":
            case "EXIT":
                this.running = false;
                System.out.println("Stopping.");
                return;
            case "STEP":
                this.cpu.step();
                return;
            case "RUN":
                this.cpu.stepUntilStop();
                return;
            case "LOADROM":
                try {
                    S19loader s19loader = S19loader.load(afterFirstWord, S19loader.TYPE_ROM);
                    for(MemorySet mem: s19loader.memories){
                        cpu.mem.attach(mem);
                    }
                    System.out.println(afterFirstWord+" loaded as ROM.");
                    if(s19loader.startAddress != null)
                        cpu.R_PC = s19loader.startAddress.shortValue();
                } catch (IOException e) {
                    System.out.println("An error occured loading the file:");
                    System.out.println(e);
                }
                break;
            case "LOADRAM":
                try {
                    S19loader s19loader = S19loader.load(afterFirstWord, S19loader.TYPE_RAM);
                    for(MemorySet mem: s19loader.memories){
                        cpu.mem.attach(mem);
                    }
                    System.out.println(afterFirstWord+" loaded as RAM.");
                    if(s19loader.startAddress != null)
                        cpu.R_PC = s19loader.startAddress.shortValue();
                } catch (IOException e) {
                    System.out.println("An error occured loading the file:");
                    System.out.println(e);
                }
                break;
            case "INSPECT": // inspect memory location
                int addr;
                try {
                    if(afterFirstWord.length()>0 && afterFirstWord.charAt(0)=='x'){ // x is hex
                        addr = Integer.parseInt(afterFirstWord.substring(1), 16);
                    }
                    else if(afterFirstWord.length()>1 && afterFirstWord.charAt(1)=='x'){ // 0x is hex
                        addr = Integer.parseInt(afterFirstWord.substring(2), 16);
                    }
                    else {
                        addr = Integer.parseInt(afterFirstWord, 10);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Address not recognized: "+afterFirstWord);
                    break;
                }
                byte val = cpu.fetch((short)addr);
                System.out.println("value at specified address: "+val+" (0x"+Util.byte_as_hex(val)+")");
                break;
            case "FLAG":
                switch(afterFirstWord){
                    case "A":
                        System.out.println("Auxiliary carry flag: "+(cpu.R_S&CPU.F_A));
                        break;
                    case "C":
                        System.out.println("Carry flag: "+(cpu.R_S&CPU.F_C));
                        break;
                    case "I":
                        System.out.println("Interrupt flag: "+(cpu.R_S&CPU.F_I));
                        break;
                    case "O":
                        System.out.println("Overflow flag: "+(cpu.R_S&CPU.F_O));
                        break;
                    case "S":
                        System.out.println("Sign flag: "+(cpu.R_S&CPU.F_S));
                        break;
                    case "Z":
                        System.out.println("Zero flag: "+(cpu.R_S&CPU.F_Z));
                        break;
                    default:
                        System.out.println("Unknown flag: "+afterFirstWord);

                }
                break;
            case "REG":
                Short s = null;
                Byte v = null;
                switch(afterFirstWord.toUpperCase().trim()){
                    case "A":
                        v = this.cpu.R_A;
                        break;
                    case "B":
                        v = this.cpu.R_B;
                        break;
                    case "S":
                        v = this.cpu.R_S;
                        break;
                    case "SP":
                        s = this.cpu.R_SP;
                        break;
                    case "PC":
                        s = this.cpu.R_PC;
                        break;
                    case "IDX":
                        s = this.cpu.R_IDX;
                        break;

                }
                if(v!=null){
                    System.out.println("Contents of specified register: "+v+" (0x"+Util.byte_as_hex(v)+")");
                    break;
                }
                if(s!=null){
                    System.out.println("Contents of specified register: "+s+" ("+Util.short_as_hex(s)+")");
                    break;
                }
                System.out.println("Unknown register: "+afterFirstWord);
                break;
            case "RESET":
                this.reset();
                System.out.println("System reset.");
                break;

            default:
                System.out.println("Unknown command: "+firstWord);
                return;
        }
    }

    public static void main(String[] args){
        UI ui = new UI();
        Scanner input = new Scanner(System.in);
        while(ui.running){
            System.out.print("> ");
            ui.parseCommand(input.nextLine());
        }
    }


}
