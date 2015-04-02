package passambler.pack.std.function;

import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionToBool extends Value implements Function {
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
        try {
            return new ValueBool(Boolean.valueOf(context.getArgument(0).toString()));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
