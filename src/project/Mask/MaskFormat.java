package project.Mask;

import java.util.ArrayList;
import java.util.List;

public class MaskFormat {
    private final String format;
    private final List<Character> types = new ArrayList<>();
    private final List<Integer> masks = new ArrayList<>();

    public MaskFormat(String format){
        this.format = format;

        // add types
        for(Character c: format.toCharArray()) {
            tryAddType(c);
        }

        // set type masks
        for(Character c: format.toCharArray()){ // loop in reverse order
            for(int i=0;i<types.size();i++){
                masks.set(i, masks.get(i)<<1); // shift left once
                if(types.get(i)==c)
                    masks.set(i, masks.get(i)|1); // set bit if same type
            }
        }
    }

    private void tryAddType(char c){
        for(char t: types){
            if(t==c)
                return;
        }
        // add defaults if not in types already
        types.add(c);
        masks.add(0);
    }

    public MaskGroup of(int value){
        return new MaskGroup(types, value, masks);
    }
}
