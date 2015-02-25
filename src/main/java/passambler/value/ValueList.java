package passambler.value;

import java.util.ArrayList;
import java.util.List;

public class ValueList extends Value implements IndexedValue {
    protected List<Value> list = new ArrayList<>();

    @Override
    public List<Value> getValue() {
        return list;
    }

    @Override
    public Value getIndex(Value key) {
        return list.get(((ValueNum) key).getValue().intValue());
    }

    @Override
    public void setIndex(Value key, Value value) {
        list.set(((ValueNum) key).getValue().intValue(), value);
    }

    @Override
    public int getIndexCount() {
        return list.size();
    }

    @Override
    public boolean equals(Value value) {
        if (value instanceof ValueList) {
            ValueList givenList = (ValueList) value;

            if (givenList.getIndexCount() != getIndexCount()) {
                return false;
            }

            for (int i = 0; i < getIndexCount(); ++i) {
                Value valueInCurrentList = list.get(i);
                Value valueInList = givenList.getValue().get(i);

                if (!valueInCurrentList.equals(valueInList)) {
                    return false;
                }
            }

            return true;
        }

        return super.equals(value);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
