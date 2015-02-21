package passambler.value;

import java.util.HashMap;
import java.util.Map;
import passambler.extension.std.ExtensionStd;
import passambler.procedure.Procedure;
import passambler.lexer.Token;

public class Value {
    protected Map<String, Property> properties = new HashMap();

    protected Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Property getProperty(String key) {
        return properties.get(key);
    }

    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }

    public void setProperty(String key, Value value) {
        properties.put(key, new Property(value));
    }

    public void setProperty(String key, Property property) {
        properties.put(key, property);
    }

    public Value onOperator(Value value, Token.Type tokenType) {
        switch (tokenType) {
            case EQUAL:
                return new ValueBool(getValue().equals(value.getValue()));
            case NEQUAL:
                return new ValueBool(!getValue().equals(value.getValue()));
            case ASSIGN:
                return value;
        }

        return null;
    }

    @Override
    public String toString() {
        return value == null ? ExtensionStd.VALUE_NIL.toString() : value.toString();
    }
}
