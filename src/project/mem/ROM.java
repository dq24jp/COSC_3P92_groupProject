package project.mem;

import java.util.Arrays;

public class ROM implements MemorySet{

    int baseAddress;

    private final byte[] mem;

    public ROM(int baseAddress, byte[] dat){
        this.baseAddress = baseAddress;
        this.mem = Arrays.copyOf(dat, dat.length);
    }
    public static ROM from_ints(int baseAddress, int[] dat_int){
        byte[] dat = new byte[dat_int.length];
        for(int i=0;i< dat_int.length;i++){
            dat[i]=(byte)dat_int[i];
        }
        return new ROM(baseAddress, dat);
    }

    @Override
    public boolean isMe(int address) {
        return address>=this.baseAddress && address<this.baseAddress+this.mem.length;
    }

    @Override
    public boolean write(int address, byte data) {
        return false; // no writey the ROMy
    }

    @Override
    public Byte read(int address) {
        if(!isMe(address)) return null;
        return this.mem[address-this.baseAddress];
    }
}
