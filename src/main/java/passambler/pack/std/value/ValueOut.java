package passambler.pack.std.value;

import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.WriteableValue;

public class ValueOut extends Value implements WriteableValue {
    @Override
    public void write(Value value) throws EngineException {
        System.out.print(value);
    }
}
