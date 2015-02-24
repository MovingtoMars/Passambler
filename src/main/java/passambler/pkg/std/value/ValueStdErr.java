package passambler.pkg.std.value;

import passambler.value.Value;
import passambler.value.ValueWriteHandler;

public class ValueStdErr extends ValueWriteHandler {
    @Override
    public void write(Value value) {
        System.err.print(value);
    }
}
