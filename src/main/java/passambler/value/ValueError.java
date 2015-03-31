package passambler.value;

public class ValueError extends Value {
    public ValueError(Value value) {
        setValue(value.toString());
    }

    public ValueError(Exception exception) {
        setValue(exception.toString());
    }

    public ValueError(String message) {
        setValue(message);
    }

    @Override
    public String getValue() {
        return (String) value;
    }
}
