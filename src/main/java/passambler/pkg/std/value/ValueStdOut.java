package passambler.pkg.std.value;

import passambler.value.Value;
import passambler.value.WriteHandler;

public class ValueStdOut extends Value implements WriteHandler {
    @Override
    public void write(Value value) {
        System.out.print(value);
    }
}
