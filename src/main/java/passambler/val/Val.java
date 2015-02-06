package passambler.val;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.scanner.Token;

public abstract class Val {
    public static ValNil nil = new ValNil();

    protected boolean locked = false;
    
    protected Map<String, Object> properties = new HashMap();
    
    protected Object value;

    public Val() {
        setProperty("str", () -> new ValString(value.toString()));
        
        if (this instanceof IndexAccess) {
            IndexAccess indexAccess = (IndexAccess) this;
            
            setProperty("size", () -> new ValNumber(indexAccess.getIndexCount()));
            
            setProperty("set", new Function() {
                @Override
                public int getArguments() {
                    return 2;
                }

                @Override
                public boolean isArgumentValid(Val value, int argument) {
                    switch (argument) {
                        case 0:
                            return value instanceof ValNumber;
                        case 1:
                            return value instanceof Val;
                        default:
                            return false;
                    }
                }

                @Override
                public Val invoke(Parser parser, Val... arguments) throws ParserException {
                    int index = ((ValNumber) arguments[0]).getValueAsInteger();

                    if (index < 0 || index > indexAccess.getIndexCount() - 1) {
                        throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, index, indexAccess.getIndexCount());
                    }

                    indexAccess.setIndex(index, arguments[1]);

                    return Val.this;
                }
            });
        }
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public Val lock() {
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
    
    public Val getProperty(String key) {
        return properties.get(key) instanceof DynamicProperty ? ((DynamicProperty) properties.get(key)).getValue() : (Val) properties.get(key);
    }
    
    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }
    
    public void setProperty(String key, Val value) {
        properties.put(key, value);
    }
    
    public void setProperty(String key, DynamicProperty dynamicProperty) {
        properties.put(key, dynamicProperty);
    }
    
    public void setProperty(String key, Function function) {
        properties.put(key, new ValBlock(null, null) {
            @Override
            public int getArguments() {
                return function.getArguments();
            }

            @Override
            public boolean isArgumentValid(Val value, int argument) {
                return function.isArgumentValid(value, argument);
            }

            @Override
            public Val invoke(Parser parser, Val... arguments) throws ParserException {
                return function.invoke(parser, arguments);
            }
        });
    }

    public Val onOperator(Val value, Token.Type tokenType) {
        return null;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
