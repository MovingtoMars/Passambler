package passambler.module.std.value;

import passambler.value.Value;
import passambler.value.Writeable;

public class ErrValue extends Value implements Writeable {
    @Override
    public void write(boolean line, Value value) {
        System.err.print(value + (line ? "\n" : ""));
    }
}
