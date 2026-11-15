package project.mem;

public interface MemorySet {
    /// returns if this MemorySet covers the given address
    public boolean isMe(int address);
    /// tries to write (data) to (address). Returns success
    public boolean write(int address, byte data);
    /// tries to read from (address). Returns value if successful, or null if cannot.
    public Byte read(int address);
}
