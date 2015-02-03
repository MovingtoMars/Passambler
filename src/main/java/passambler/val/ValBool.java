package passambler.val;

public class ValBool extends Val {
    public ValBool(Boolean value) {
        setValue(value);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) value;
    }
}
