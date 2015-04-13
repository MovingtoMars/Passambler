package passambler.pack.std.value;

import passambler.value.Value;
import passambler.value.WriteableValue;

public class OutValue extends Value implements WriteableValue {
    @Override
    public void write(Value value) {
        System.out.print(value);
    }
}
