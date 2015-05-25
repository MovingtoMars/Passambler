package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ListValue;

public class ShiftFunction extends Value implements Function {
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
        ListValue list = (ListValue) context.getArgument(0);

        Value first = list.getValue().get(0);

        list.getValue().remove(0);

        return first;
    }
}
