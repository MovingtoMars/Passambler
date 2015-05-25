package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ListValue;
import passambler.value.NumberValue;

public class SliceFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 3;
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
        ListValue list = (ListValue) context.getArgument(0);

        ListValue subList = new ListValue();

        for (int i = ((NumberValue) context.getArgument(1)).getValue().intValue(); i <= ((NumberValue) context.getArgument(2)).getValue().intValue(); ++i) {
            subList.getValue().add(list.getValue().get(i));
        }

        return subList;
    }
}
