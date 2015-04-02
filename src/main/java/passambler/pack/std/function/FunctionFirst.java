package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.ValueList;
import passambler.value.Value;

public class FunctionFirst extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueList;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return ((ValueList) context.getArgument(0)).getValue().get(0);
    }
}
