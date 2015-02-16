package passambler.value;

import passambler.function.Function;

public class ValueClass extends Value {
    private Function constructor;
    
    public void setConstructor(Function function) {
        constructor = function;
    }
    
    public Function getConstructor() {
        return constructor;
    }
}
