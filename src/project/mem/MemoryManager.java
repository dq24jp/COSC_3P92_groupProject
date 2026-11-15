package project.mem;

import java.util.LinkedList;
import java.util.List;

/// A MemorySet that contains other MemorySets.
public class MemoryManager implements MemorySet{
    private final List<MemorySet> sets = new LinkedList<>();

    public MemoryManager(){}

    /// add a MemorySet to this manager
    /// Please don't make a circle.
    public void attach(MemorySet mem){
        this.sets.addFirst(mem);
    }

    @Override
    public boolean isMe(int address) {
        for(MemorySet set: sets){
            if(set.isMe(address)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean write(int address, byte data) {
        for(MemorySet set: sets){
            if(set.isMe(address)){
                return set.write(address, data);
            }
        }
        return false;
    }

    @Override
    public Byte read(int address) {
        for(MemorySet set: sets){
            if(set.isMe(address)){
                return set.read(address);
            }
        }
        return null;
    }
}
