package passambler.value;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.lexer.Token;

public class Value {
    public static ValueNil nil = new ValueNil();

    protected boolean locked = false;
    
    protected Map<String, Object> properties = new HashMap();
    
    protected Object value;

    public Value() {
        setProperty("str", () -> new ValueStr(toString()));
        
        if (this instanceof IndexedValue) {
            IndexedValue indexAccess = (IndexedValue) this;
            
            setProperty("size", () -> new ValueNum(indexAccess.getIndexCount()));
            
            setProperty("set", new Function() {
                @Override
                public int getArguments() {
                    return 2;
                }

                @Override
                public boolean isArgumentValid(Value value, int argument) {
                    switch (argument) {
                        case 0:
                            return value instanceof ValueNum;
                        case 1:
                            return value instanceof Value;
                        default:
                            return false;
                    }
                }

                @Override
                public Value invoke(Parser parser, Value... arguments) throws ParserException {
                    int index = ((ValueNum) arguments[0]).getValueAsInteger();

                    if (index < 0 || index > indexAccess.getIndexCount() - 1) {
                        throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, index, indexAccess.getIndexCount());
                    }

                    indexAccess.setIndex(index, arguments[1]);

                    return Value.this;
                }
            });
        }
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public Value lock() {
        locked = true;
        
        return this;
    }
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (locked) {
            throw new RuntimeException("Value is locked");
        }
        
        this.value = value;
    }
    
    public Value getProperty(String key) {
        return properties.get(key) instanceof DynamicProperty ? ((DynamicProperty) properties.get(key)).getValue() : (Value) properties.get(key);
    }
    
    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }
    
    public void setProperty(String key, Value value) {
        properties.put(key, value);
    }
    
    public void setProperty(String key, DynamicProperty dynamicProperty) {
        properties.put(key, dynamicProperty);
    }
    
    public void setProperty(String key, Function function) {
        properties.put(key, ValueBlock.transform(function));
    }

    public Value onOperator(Value value, Token.Type tokenType) {
        switch (tokenType) {
            case EQUAL:
                return new ValueBool(getValue().equals(value.getValue()));
            case NEQUAL:
                return new ValueBool(!getValue().equals(value.getValue()));
        }
        
        return null;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
