package passambler.bundle.std.value;

import passambler.value.Value;
import passambler.value.WriteableValue;

public class OutValue extends Value implements WriteableValue {
    @Override
    public void write(boolean line, Value value) {
        System.out.print(value + (line ? "\n" : ""));
    }
}
