package passambler.value;

import java.util.LinkedHashMap;
import java.util.Map;

public class DictValue extends Value {
    protected Map<Value, Value> dict = new LinkedHashMap();

    @Override
    public Map<Value, Value> getValue() {
        return dict;
    }

    public Value getEntry(Value key) {
        return dict.entrySet().stream().filter(e -> e.getKey().getValue().equals(key.getValue())).findFirst().orElse(null).getValue();
    }

    public void setEntry(Value key, Value value) {
        // If the key already exists, remove it, so the key can be overriden
        dict.entrySet().stream().filter(e -> e.getKey().getValue().equals(key.getValue())).forEach(e -> dict.remove(e.getKey()));

        dict.put(key, value);
    }

    @Override
    public String toString() {
        return dict.toString();
    }
}
