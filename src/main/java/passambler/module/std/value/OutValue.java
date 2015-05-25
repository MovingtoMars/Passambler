package passambler.module.std.value;

import passambler.value.Value;
import passambler.value.Writeable;

public class OutValue extends Value implements Writeable {
    @Override
    public void write(boolean line, Value value) {
        System.out.print(value + (line ? "\n" : ""));
    }
}
