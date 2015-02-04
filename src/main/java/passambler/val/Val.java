package passambler.val;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.parser.Scope;
import passambler.scanner.Token;

public abstract class Val {
    public static ValNil nil = new ValNil();

    protected boolean locked = false;
    
    protected Map<String, Object> properties = new HashMap();
    
    protected Object value;

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
        properties.put(key, new ValBlock(new Scope(), null) {
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
