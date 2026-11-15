package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

import project.mem.MemorySet;
import project.mem.RAM;
import project.mem.ROM;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class S19loader {


    public static final int TYPE_RAM = 1;
    public static final int TYPE_ROM = 2;

    public MemorySet[] memories;
    public int startAddress;

    private S19loader(MemorySet[] memories, int startAddress){
        this.memories = memories;
        this.startAddress = startAddress;
    }
    public static S19loader load(String filename, int memtype) throws IOException{

        if(memtype==TYPE_RAM){}        // RAM
        else if(memtype==TYPE_ROM){}   // ROM
        else{
            System.out.println("Unknown memory type: "+memtype);
            return null;
        }


        Integer startAddress = null;

        List<MemorySet> memories = new LinkedList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of((filename)))){

            String line;

            while ((line = reader.readLine()) != null){
                line = line.trim();

                if (line.isEmpty()) continue;

                String type = line.substring(0, 2);

                int count = Integer.parseInt(line.substring(2, 4), 16);



                if (type.equals("S1")){
                    int address = Integer.parseInt(line.substring(4, 8), 16);

                    int dataLength = count - 3;

                    byte[] memdata = new byte[dataLength];

                    for (int i = 0; i < dataLength; i++){
                        int value = Integer.parseInt(line.substring(8 + i * 2, 10 + i * 2), 16);

                        memdata[i] = (byte) value;
                    }

                    if(memtype==TYPE_RAM) {
                        memories.add(new RAM(address, memdata));
                    }
                    else if(memtype==TYPE_ROM) {
                        memories.add(new ROM(address, memdata));
                    }

                }

                else if (type.equals("S9")){
                    startAddress = Integer.parseInt(line.substring(4, 8), 16);
                    return new S19loader(memories.toArray(MemorySet[]::new), startAddress);
                }
            }
            throw new RuntimeException("Error: no S9 line found");
        }

    }
}
