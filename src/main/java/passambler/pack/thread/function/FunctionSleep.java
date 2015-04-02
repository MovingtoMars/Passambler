package passambler.pack.thread.function;

import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionSleep extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            Thread.sleep(((ValueNum) context.getArgument(0)).getValue().intValue());

            return new ValueBool(true);
        } catch (InterruptedException e) {
            throw new ErrorException(e);
        }
    }
}
