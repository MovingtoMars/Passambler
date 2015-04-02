package passambler.value.function;

import passambler.exception.EngineException;
import passambler.value.Value;

public interface Function {
    public int getArguments();

    public boolean isArgumentValid(Value value, int argument);

    public Value invoke(FunctionContext context) throws EngineException;
}
