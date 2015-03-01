package passambler.value;

import java.util.ArrayList;
import java.util.List;

public class ValueList extends Value {
    protected List<Value> list = new ArrayList<>();

    @Override
    public List<Value> getValue() {
        return list;
    }

    @Override
    public boolean equals(Value value) {
        if (value instanceof ValueList) {
            ValueList givenList = (ValueList) value;

            if (givenList.getValue().size() != list.size()) {
                return false;
            }

            for (int i = 0; i < list.size(); ++i) {
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
