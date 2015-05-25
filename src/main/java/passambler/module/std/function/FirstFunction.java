package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.ListValue;
import passambler.value.Value;

public class FirstFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ListValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return ((ListValue) context.getArgument(0)).getValue().get(0);
    }
}
