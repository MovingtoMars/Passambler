package passambler.parser;

import passambler.value.Value;

public class Argument {
    private String name;

    private Value defaultValue;

    private boolean visible;

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

    public void setPublic(boolean visible) {
        this.visible = visible;
    }

    public boolean isPublic() {
        return visible;
    }
}
