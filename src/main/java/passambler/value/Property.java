package passambler.value;

public class Property {
    private Value value;
    
    public Property() {
        this(null);
    }
    
    public Property(Value value) {
        this.value = value;
    }
    
    public Value getValue() {
        return value;
    }
}
