package passambler.pack.error.value;

import passambler.value.Value;

public class ValueError extends Value {
    public ValueError(String message) {
        setValue(message);
    }
    
    @Override
    public String getValue() {
        return (String) value;
    }
}
