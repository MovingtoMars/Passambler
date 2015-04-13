package passambler.value;

import passambler.exception.EngineException;

public interface WriteableValue {
    public void write(Value value) throws EngineException;
}
