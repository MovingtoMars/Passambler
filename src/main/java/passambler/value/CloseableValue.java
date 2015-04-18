package passambler.value;

import passambler.exception.EngineException;

public interface CloseableValue {
    public void close() throws EngineException;
}
