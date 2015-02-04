package passambler.val;

import java.util.Arrays;

public class ValArray extends Val implements IndexAccess {
    private final int size;

    private Val[] items;

    public ValArray(int size) {
        this.size = size;
        this.items = new Val[size];

        for (int i = 0; i < size; ++i) {
            this.items[i] = Val.nil;
        }
    }

    @Override
    public Val getIndex(int index) {
        return items[index];
    }

    @Override
    public void setIndex(int index, Val value) {
        items[index] = value;
    }

    @Override
    public int getIndexCount() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.toString(items);
    }
    
    @Override
    public Val getProperty(String key) {
        if (key.equals("size")) {
            return new ValNumber(getIndexCount());
        } else {
            return super.getProperty(key);
        }
    }
}
