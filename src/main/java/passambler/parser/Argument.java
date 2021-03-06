package passambler.parser;

import passambler.value.Value;

public class Argument {
    private String name;

    private Value defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Value getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Value defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return name;
    }
}
