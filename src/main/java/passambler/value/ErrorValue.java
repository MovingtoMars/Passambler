package passambler.value;

public class ErrorValue extends Value {
    public ErrorValue(Value value) {
        setValue(value.toString());
    }

    public ErrorValue(Exception exception) {
        setValue(exception.toString());
    }

    public ErrorValue(String message) {
        setValue(message);
    }

    @Override
    public String getValue() {
        return (String) value;
    }
}
