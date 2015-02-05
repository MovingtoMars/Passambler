package passambler.val;

import java.util.ArrayList;
import java.util.List;
import passambler.parser.Stream;

public class ValList extends Val implements IndexAccess, Stream {
    private List<Val> list = new ArrayList<>();
    
    @Override
    public Val getIndex(int index) {
        return list.get(index);
    }

    @Override
    public void setIndex(int index, Val value) {
        list.set(index, value);
    }

    @Override
    public int getIndexCount() {
        return list.size();
    }

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public void onStream(Val value) {
        add(value);
    }
    
    public void add(Val value) {
        if (isLocked()) {
            throw new RuntimeException("Value is locked");
        }
        
        list.add(value);
    }
}
