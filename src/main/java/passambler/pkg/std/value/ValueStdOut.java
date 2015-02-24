package passambler.pkg.std.value;

import passambler.value.Value;
import passambler.value.ValueWriteHandler;

public class ValueStdOut extends ValueWriteHandler {
    @Override
    public void write(Value value) {
        System.out.print(value);
    }
}
