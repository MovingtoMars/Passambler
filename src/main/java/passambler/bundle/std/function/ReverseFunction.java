package passambler.bundle.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ListValue;

public class ReverseFunction extends Value implements Function {
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
        ListValue value = (ListValue) context.getArgument(0);

        ListValue subList = new ListValue();

        for (int i = value.getValue().size() - 1; i >= 0; --i) {
            subList.getValue().add(value.getValue().get(i));
        }

        return subList;
    }
}
