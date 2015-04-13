package passambler.value;

import passambler.exception.EngineException;

public interface ReadableValue {
    public Value read() throws EngineException;
}
