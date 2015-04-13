package passambler.value.function;

import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.Value;
import passambler.value.ErrorValue;

public class ThrowFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        throw new ErrorException(new ErrorValue(context.getArgument(0)));
    }
}