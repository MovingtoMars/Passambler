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
    public String toString() {
        return list.toString();
    }
}
