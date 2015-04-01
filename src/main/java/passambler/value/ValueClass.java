package passambler.value;

public class ValueClass extends Value {
    private String name;

    public ValueClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
