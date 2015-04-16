package passambler.pack.std.value;

import passambler.exception.EngineException;
import passambler.value.ReadableValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class InValue extends Value implements ReadableValue {
    @Override
    public Value read() throws EngineException {
        return new StringValue(System.console().readLine());
    }
}
