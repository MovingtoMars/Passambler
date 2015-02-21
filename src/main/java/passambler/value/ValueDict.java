package passambler.value;

import java.util.HashMap;
import java.util.Map;

public class ValueDict extends Value implements IndexedValue {
    protected Map<Value, Value> dict = new HashMap();
    
    @Override
    public Map<Value, Value> getValue() {
        return dict;
    }

    @Override
    public Value getIndex(Value key) {
        for (Map.Entry<Value, Value> entry : dict.entrySet()) {
            if (entry.getKey().getValue().equals(key.getValue())) {
                return entry.getValue();
            }
        }
        
        return null;
    }

    @Override
    public void setIndex(Value key, Value value) {
        dict.put(key, value);
    }

    @Override
    public int getIndexCount() {
        return dict.size();
    }

    @Override
    public String toString() {
        return dict.toString();
    }
}
