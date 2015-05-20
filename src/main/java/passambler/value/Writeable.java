package passambler.value;

import passambler.exception.EngineException;

public interface Writeable {
    public void write(boolean line, Value value) throws EngineException;
}
