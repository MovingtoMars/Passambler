package passambler.bundle.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ListValue;
import passambler.value.NumberValue;

public class SizeFunction extends Value implements Function {
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
        return new NumberValue(((ListValue) context.getArgument(0)).getValue().size());
    }
}
