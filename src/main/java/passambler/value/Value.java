package passambler.value;

import java.util.HashMap;
import java.util.Map;
import passambler.procedure.Procedure;
import passambler.lexer.Token;

public class Value {
    public static ValueNil VALUE_NIL = new ValueNil();

    public static ValueBool VALUE_TRUE = new ValueBool(true);
    public static ValueBool VALUE_FALSE = new ValueBool(false);

    public static ValueNum VALUE_PI = new ValueNum(Math.PI);

    public static ValueOutStream VALUE_STDOUT = new ValueOutStream(System.out);
    public static ValueOutStream VALUE_STDERR = new ValueOutStream(System.err);

    public static ValueInStream VALUE_STDIN = new ValueInStream(System.in);

    protected Map<String, Property> properties = new HashMap();

    protected Object value;

    public Value() {
        setProperty("str", new Property() {
            @Override
            public Value getValue() {
                return new ValueStr(Value.this.toString());
            }
        });
    }

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

    public void setProperty(String key, Procedure procedure) {
        properties.put(key, new Property(ValueBlock.transform(procedure)));
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
        return value == null ? Value.VALUE_NIL.toString() : value.toString();
    }
}
