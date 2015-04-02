package passambler.value;

import passambler.exception.EngineException;

public interface WriteHandler {
    public void write(Value value) throws EngineException;
}
