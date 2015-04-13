package passambler.pack.thread.function;

import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.NumberValue;

public class SleepFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            Thread.sleep(((NumberValue) context.getArgument(0)).getValue().intValue());
        } catch (InterruptedException e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
