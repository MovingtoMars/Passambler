package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ListValue;
import passambler.value.NumberValue;

public class DeleteFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ListValue;
        }

        return value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ((ListValue) context.getArgument(0)).getValue().remove(((NumberValue) context.getArgument(1)).getValue().intValue());

        return null;
    }
}
