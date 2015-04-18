package passambler.value;

import passambler.exception.EngineException;

public interface ReadableValue {
    public Value read(boolean line) throws EngineException;
}
