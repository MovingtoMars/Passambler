package passambler.value;

import passambler.exception.EngineException;

public interface WriteableValue {
    public void write(boolean line, Value value) throws EngineException;
}
