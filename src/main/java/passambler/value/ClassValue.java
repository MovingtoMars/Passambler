package passambler.value;

public class ClassValue extends Value {
    private String name;

    public ClassValue(String name) {
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
