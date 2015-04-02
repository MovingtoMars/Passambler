package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueStr;

public class FunctionContains extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new ValueBool(((ValueStr) context.getArgument(0)).getValue().contains(((ValueStr) context.getArgument(1)).getValue()));
    }
}
