package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ReadableValue;

public class ReadFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ReadableValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return ((ReadableValue) context.getArgument(0)).read();
    }
}
