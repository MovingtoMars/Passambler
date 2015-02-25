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
    public boolean equals(Value value) {
        if (value instanceof ValueDict) {
            ValueDict givenDict = (ValueDict) value;

            if (givenDict.getValue().size() != getValue().size()) {
                return false;
            }

            for (Map.Entry<Value, Value> entry : getValue().entrySet()) {
                if (givenDict.getValue().containsKey(entry.getKey())) {
                    if (!givenDict.getValue().get(entry.getKey()).equals(entry.getValue())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            return true;
        }

        return super.equals(value);
    }

    @Override
    public String toString() {
        return dict.toString();
    }
}
