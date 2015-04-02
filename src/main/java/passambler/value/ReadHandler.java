package passambler.value;

import passambler.exception.EngineException;

public interface ReadHandler {
    public Value read() throws EngineException;
}
