package passambler.value;

import java.util.LinkedHashMap;
import java.util.Map;

public class DictValue extends Value {
    protected Map<Value, Value> dict = new LinkedHashMap();

    @Override
    public Map<Value, Value> getValue() {
        return dict;
    }

    // This helper method exists because by default, Map.get() doesn't compare with
    // the data of the value, it compares the value instance instead.
    // Therefore, in most cases it isn't possible to retrieve a value with Map.get().
    public Value getEntry(Value key) {
        for (Map.Entry<Value, Value> entry : dict.entrySet()) {
            if (entry.getKey().getValue().equals(key.getValue())) {
                return entry.getValue();
            }
        }

        return null;
    }

    public void setEntry(Value key, Value value) {
        for (Map.Entry<Value, Value> entry : dict.entrySet()) {
            if (entry.getKey().getValue().equals(key.getValue())) {
                dict.remove(entry.getKey());
            }
        }

        dict.put(key, value);
    }

    @Override
    public String toString() {
        return dict.toString();
    }
}
