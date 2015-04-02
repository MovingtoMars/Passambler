package passambler.pack.std.value;

import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.WriteHandler;

public class ValueOut extends Value implements WriteHandler {
    @Override
    public void write(Value value) throws EngineException {
        System.out.print(value);
    }
}
