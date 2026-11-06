package project.mem;

public class RAM implements MemorySet{

    int baseAddress;

    private final byte[] mem;

    public RAM(int baseAddress, int size){
        this.baseAddress = baseAddress;
        this.mem = new byte[size];
    }

    @Override
    public boolean isMe(int address) {
            return address>=this.baseAddress && address<this.baseAddress+this.mem.length;
    }

    @Override
    public boolean write(int address, byte data) {
        if(!isMe(address)) return false;
        this.mem[address-this.baseAddress]=data;
        return true;
    }

    @Override
    public Byte read(int address) {
        if(!isMe(address)) return null;
        return this.mem[address-this.baseAddress];
    }
}
