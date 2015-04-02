package passambler.value.function;

import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.Value;
import passambler.value.ValueError;

public class FunctionThrow extends Value implements Function {
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
        throw new ErrorException(new ValueError(context.getArgument(0)));
    }
}