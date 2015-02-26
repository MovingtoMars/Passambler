package passambler.value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValueDict extends Value implements IndexedValue {
    protected Map<Value, Value> dict = new LinkedHashMap();

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
    public boolean equals(Value value) {
        if (value instanceof ValueDict) {
            List valuesLeft = new ArrayList<>(getValue().values());
            List valuesRight = new ArrayList<>(((ValueDict) value).getValue().values());

            if (!valuesLeft.equals(valuesRight)) {
                return false;
            } else {
                List keysLeft = new ArrayList<>(getValue().keySet());
                List keysRight = new ArrayList<>(((ValueDict) value).getValue().keySet());

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
