package project;

import project.mem.MemoryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CPU extends Hookable<String, CPU>{
    // ///////// //
    // REGISTERS //
    // ///////// //
    /// accumulator X register
    byte R_X;
    /// accumulator Y register
    byte R_Y;
    /// index register
    short R_IDX;
    /// stack pointer register
    short R_SP;
    /// program counter register
    short R_PC;
    /// status register
    byte R_S = (byte)0b1100_0000;

    // ///////// //
    //   FLAGS   //
    // ///////// //
    /// Carry flag
    static final byte F_C = 0x01;
    /// Overflow flag
    static final byte F_O = 0x02;
    /// Zero flag
    static final byte F_Z = 0x04;
    /// Sign flag (1=negative, 0=positive)
    static final byte F_S = 0x08;
    /// Interrupt flag
    static final byte F_I = 0x10;
    /// Adjacent carry flag
    static final byte F_A = 0x20;

    /// list of instructions
    public List<Instruction> instructions = new ArrayList<>();


    public final MemoryManager mem = new MemoryManager();
    /// value to be returned when no memory exists at an address
    private static final byte OOB_MEM_VALUE = 0x00;

    public CPU(){

    }

    /// executes next instruction
    public InstructionReturn step(){
        byte opcode = this.fetchNext();

        try {
            for (Instruction i : instructions) {
                if (i.opcode() == opcode) {
                    this.run_hooks("PRE_INSTRUCTION", this);
                    InstructionReturn instr_return = i.execfunction().apply(this);
                    this.run_hooks("POST_INSTRUCTION", this);
                    if (instr_return == null)
                        return new InstructionReturn();
                    return instr_return;
                }
            }
        } catch (Exception e) {
            System.out.println("AAAHHH error in instruction!!!! "+opcode);
            throw new RuntimeException(e);
        }
        throw new RuntimeException("AAAHHH unknown instruction: "+opcode);
    }

    /// executes instructions until one stops the cpu
    public void stepUntilStop(){
        while(true){
            if(step().isStopped){ // stop if stopped
                return;
            }
        }
    }

    public void addInstruction(Instruction i){
        this.instructions.addFirst(i);
    }

    public void addISA(Instruction[] isa){
        for(Instruction i: isa) {
            this.instructions.addFirst(i);
        }
    }
    public void addISA(Collection<Instruction> isa){
        this.instructions.addAll(isa);
    }





    byte fetch(short address){
        Byte b = mem.read(address);
        if(b==null){
            return OOB_MEM_VALUE;
        }
        return b;
    }

    /// fetch next byte and increment program counter
    byte fetchNext(){
        return this.fetch(R_PC++);
    }

    void push_to_stack(byte b){
        this.mem.write(this.R_SP--, b); // stack grows downwards
    }
    byte pop_from_stack(){
        return this.mem.read(--this.R_SP);
    }

    /// sets the relevant flag bit to value
    void set_flag(byte flag, boolean value){
        this.R_S = (byte)((this.R_S & flag) | (value?flag:0));
    }
    /// sets flag bit to true
    void set_flag(byte flag){
        this.set_flag(flag, true);
    }

    /// automatically updates S, Z, C flags from output value
    void update_ZS_flags_from_output(int output){
        this.set_flag(F_C, output<-128 || output>127);
        this.set_flag(F_S, output<0);
        this.set_flag(F_Z, output==0);
    }

}
