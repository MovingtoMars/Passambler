package passambler.bundle.std.value;

import passambler.value.Value;
import passambler.value.WriteableValue;

public class ErrValue extends Value implements WriteableValue {
    @Override
    public void write(boolean line, Value value) {
        System.err.print(value + (line ? "\n" : ""));
    }
}
