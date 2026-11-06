package project;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Hookable<T extends Comparable<T>, arg_t> {
    private final List<Consumer<arg_t>> functions = new ArrayList<>();
    private final List<T> types = new ArrayList<>();

    public void add_hook(T hook_name, Consumer<arg_t> function){
        this.types.add(hook_name);
        this.functions.add(function);
    }
    public void run_hooks(T hook_name, arg_t argument){
        for(int i=0;i<this.functions.size();i++){
            if(this.types.get(i).equals(hook_name)){
                this.functions.get(i).accept(argument);
            }
        }
    }
}
