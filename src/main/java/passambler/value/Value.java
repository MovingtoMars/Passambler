package passambler.value;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
import passambler.function.FunctionSimple;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.lexer.Token;

public class Value {
    public static ValueNil VALUE_NIL = (ValueNil) new ValueNil().lock();
        
    public static ValueBool VALUE_TRUE = (ValueBool) new ValueBool(true).lock();
    public static ValueBool VALUE_FALSE = (ValueBool) new ValueBool(false).lock();
    
    public static ValueNum VALUE_PI = (ValueNum) new ValueNum(Math.PI).lock();
    
    public static ValueOutStream VALUE_STDOUT = (ValueOutStream) new ValueOutStream(System.out).lock();
    public static ValueOutStream VALUE_STDERR = (ValueOutStream) new ValueOutStream(System.err).lock();
    
    public static ValueInStream VALUE_STDIN = (ValueInStream) new ValueInStream(System.in).lock();
    
    protected boolean locked = false;
    
    protected Map<String, Property> properties = new HashMap();
    
    protected Object value;

    public Value() {
        setProperty("str", new Property() {
            @Override
            public Value getValue() {
                return new ValueStr(Value.this.toString());
            }
        });
        
        if (this instanceof IndexedValue) {
            IndexedValue indexedValue = (IndexedValue) this;
            
            setProperty("size", new Property() {
                @Override
                public Value getValue() {
                    return new ValueNum(indexedValue.getIndexCount());
                }
            });
            
            setProperty("set", new Function() {
                @Override
                public int getArguments() {
                    return 2;
                }

                @Override
                public boolean isArgumentValid(Value value, int argument) {
                    if (argument == 0) {
                        return value instanceof ValueNum;
                    } else if (argument == 1) {
                        return true;
                    }
                    
                    return false;
                }

                @Override
                public Value invoke(Parser parser, Value... arguments) throws ParserException {
                    int index = ((ValueNum) arguments[0]).getValueAsInteger();

                    if (index < 0 || index > indexedValue.getIndexCount() - 1) {
                        throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, index, indexedValue.getIndexCount());
                    }

                    indexedValue.setIndex(index, arguments[1]);

                    return Value.this;
                }
            });
            
            setProperty("empty", new FunctionSimple() {
                @Override
                public Value getValue() {
                    return new ValueBool(indexedValue.getIndexCount() == 0);
                }
            });
            
            setProperty("first", new Property() {
                @Override
                public Value getValue() {
                    return indexedValue.getIndex(0);
                }
            });
            
            setProperty("last", new Property() {
                @Override
                public Value getValue() {
                    return indexedValue.getIndex(indexedValue.getIndexCount() - 1);
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
            throw new RuntimeException("value is locked");
        }
        
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
    
    public void setProperty(String key, Function function) {
        properties.put(key, new Property(ValueBlock.transform(function)));
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
