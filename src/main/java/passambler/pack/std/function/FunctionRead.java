package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ReadHandler;

public class FunctionRead extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ReadHandler;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return ((ReadHandler) context.getArgument(0)).read();
    }
}
