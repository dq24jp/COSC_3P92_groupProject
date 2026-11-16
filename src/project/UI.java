package project;

import project.mem.MemorySet;

import java.io.IOException;
import java.util.Scanner;

public class UI {

    boolean running = true;
    CPU cpu;

    public UI(){
        this(new CPU());
    }
    public UI(CPU cpu){
        this.cpu = cpu;
    }

    public void parseCommand(String command){
        command = command.toUpperCase().trim();

        String[] words = command.split(" ");
        String firstWord = words[0];
        String afterFirstWord = command.substring(command.indexOf(" ")+1).trim();

        switch(words[0]){
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
                    cpu.R_PC = (short) s19loader.startAddress;
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
                    cpu.R_PC = (short) s19loader.startAddress;
                } catch (IOException e) {
                    System.out.println("An error occured loading the file:");
                    System.out.println(e);
                }
                break;
            case "INSPECT": // inspect memory location
                int addr;
                try {
                    if(afterFirstWord.charAt(0)=='x'){ // x is hex
                        addr = Integer.parseInt(afterFirstWord.substring(1), 16);
                    }
                    else {
                        addr = Integer.parseInt(afterFirstWord, 10);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Address not recognized: "+afterFirstWord);
                    break;
                }
                System.out.println("value at specified address: "+cpu.fetch((short)addr));
                break;
            case "FLAG":
                switch(afterFirstWord){
                    case "A":
                        System.out.println("Flag A: "+(cpu.R_S&CPU.F_A));
                        break;
                    case "C":
                        System.out.println("Carry flag: "+(cpu.R_S&CPU.F_C));
                        break;
                    case "I":
                        System.out.println("Interrupt flag: "+(cpu.R_S&CPU.F_I));
                        break;
                    case "O":
                        System.out.println("Flag O: "+(cpu.R_S&CPU.F_O));
                        break;
                    case "S":
                        System.out.println("Flag S: "+(cpu.R_S&CPU.F_S));
                        break;
                    case "Z":
                        System.out.println("Zero flag: "+(cpu.R_S&CPU.F_Z));
                        break;
                    default:
                        System.out.println("Unknown flag: "+afterFirstWord);

                }
                break;
            case "RESET":
                this.cpu = new CPU();
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
