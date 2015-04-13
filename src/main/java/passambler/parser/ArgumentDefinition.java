package passambler.parser;

import passambler.parser.typehint.AnyTypehint;
import passambler.parser.typehint.Typehint;
import passambler.value.Value;

public class ArgumentDefinition {
    private String name;

    private Value defaultValue;
    
    private Typehint typehint = new AnyTypehint();

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
    
    public Typehint getTypehint() {
        return typehint;
    }

    public void setTypehint(Typehint typehint) {
        this.typehint = typehint;
    }
}