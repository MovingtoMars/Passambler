package passambler.pack.std.value;

import passambler.value.Value;
import passambler.value.WriteHandler;

public class ValueErr extends Value implements WriteHandler {
    @Override
    public void write(Value value) {
        System.err.print(value);
    }
}
