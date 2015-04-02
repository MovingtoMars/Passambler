package passambler.value.function;

import passambler.exception.EngineException;
import passambler.value.Value;

public abstract class FunctionSimple extends Value implements Function {
    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return false;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return getReturnValue();
    }

    public abstract Value getReturnValue();
}
