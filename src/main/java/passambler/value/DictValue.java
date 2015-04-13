package passambler.value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
        dict.put(key, value);
    }

    @Override
    public boolean equals(Value value) {
        if (value instanceof DictValue) {
            List valuesLeft = new ArrayList<>(getValue().values());
            List valuesRight = new ArrayList<>(((DictValue) value).getValue().values());

            if (!valuesLeft.equals(valuesRight)) {
                return false;
            } else {
                List keysLeft = new ArrayList<>(getValue().keySet());
                List keysRight = new ArrayList<>(((DictValue) value).getValue().keySet());

                return keysLeft.equals(keysRight);
            }
        }

        return super.equals(value);
    }

    @Override
    public String toString() {
        return dict.toString();
    }
}
