package passambler.value;

import java.util.ArrayList;
import java.util.List;

public class ListValue extends Value {
    protected List<Value> list = new ArrayList<>();

    @Override
    public List<Value> getValue() {
        return list;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ListValue) {
            ListValue givenList = (ListValue) object;

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

        return super.equals(object);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
