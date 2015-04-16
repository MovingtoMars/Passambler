package passambler.bundle.std.function;

import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.BooleanValue;

public class ToBoolFunction extends Value implements Function {
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
            return new BooleanValue(Boolean.valueOf(context.getArgument(0).toString()));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
