package passambler.value;

import passambler.exception.EngineException;

public interface Readable {
    public Value read(boolean line) throws EngineException;
}
