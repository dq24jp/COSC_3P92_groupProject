package project;

/// object to contain data about an instruction's execution.
public class InstructionReturn {
    /// whether the instruction stops the cpu
    public boolean isStopped;

    /// creates with default properties
    public InstructionReturn(){
        this(false);
    }

    /// creates with given properties
    public InstructionReturn(boolean isStopped){
        this.isStopped=isStopped;
    }
}
