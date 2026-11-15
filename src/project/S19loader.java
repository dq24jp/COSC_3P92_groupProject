package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

import project.mem.MemorySet;

import java.nio.file.Path;

public class S19loader {
    public Integer load(String filename, MemorySet memory) throws IOException{
        Integer startAddress = null;

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

                    for (int i = 0; i < dataLength; i++){
                        int value = Integer.parseInt(line.substring(8 + i * 2, 10 + i * 2), 16);

                        memory.load(address + i, (byte) value);   
                    }

                }

                else if (type.equals("S9")){
                    startAddress = Integer.parseInt(line.substring(4, 8), 16);
                }
            }
        }
    
    return startAddress;
    }
}
