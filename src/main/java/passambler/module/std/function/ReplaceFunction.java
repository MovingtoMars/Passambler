package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.StringValue;

public class ReplaceFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new StringValue(((StringValue) context.getArgument(0)).toString().replace(((StringValue) context.getArgument(1)).toString(), ((StringValue) context.getArgument(2)).toString()));
    }
}
