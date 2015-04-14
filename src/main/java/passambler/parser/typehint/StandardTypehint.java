package passambler.parser.typehint;

import passambler.value.Value;

public class StandardTypehint implements Typehint {
    private Class<? extends Value> valueClass;

    public StandardTypehint(Class<? extends Value> valueClass) {
        this.valueClass = valueClass;
    }

    @Override
    public boolean matches(Value value) {
        return value != null && value.getClass().isAssignableFrom(valueClass);
    }
}
