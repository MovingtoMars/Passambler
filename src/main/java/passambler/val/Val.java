package passambler.val;

import java.util.HashMap;
import java.util.Map;
import passambler.scanner.Token;

public abstract class Val {
    public static ValNil nil = new ValNil();

    protected Map<String, Val> properties = new HashMap();
    
    protected Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public Val getProperty(String key) {
        return properties.get(key);
    }
    
    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }
    
    public void setProperty(String key, Val value) {
        properties.put(key, value);
    }

    public Val onOperator(Val value, Token.Type tokenType) {
        return null;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
