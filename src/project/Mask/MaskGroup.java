package project.Mask;

import java.util.List;

public class MaskGroup {
    private final List<Character> types;
    private final int value;
    private final List<Integer> masks;

    MaskGroup(List<Character> types, int value, List<Integer> masks){
        this.types = types;
        this.value = value;
        this.masks = masks;
    }

    /// returns the value with relevant mask
    public int Get(Character key){
        for(int i=0;i<this.types.size();i++){
            if(types.get(i)==key){
                return value & this.masks.get(i);
            }
        }
        throw new IndexOutOfBoundsException("Key not found:"+key);
    }
    /// returns the value with relevant mask. Output is LSB-aligned
    public int GetCondensed(Character key){
        for(int i=0;i<this.types.size();i++){
            if(types.get(i)==key){
                int v = value & this.masks.get(i);
                int m = this.masks.get(i);
                for(int j=0;j<32&&(m&1)==0;j++){ // right-align
                    v>>=1;
                    m>>=1;
                }
                return v;
            }
        }
        throw new IndexOutOfBoundsException("Key not found:"+key);
    }

}
