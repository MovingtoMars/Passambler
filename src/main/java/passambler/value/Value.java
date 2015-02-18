package passambler.value;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
import passambler.function.FunctionSimple;
import passambler.parser.Parser;
import passambler.parser.ParserException;
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
