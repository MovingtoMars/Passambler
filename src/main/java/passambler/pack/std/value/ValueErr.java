package passambler.pack.std.value;

import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.WriteableValue;

public class ValueErr extends Value implements WriteableValue {
    @Override
    public void write(Value value) throws EngineException {
        System.err.print(value);
    }
}
