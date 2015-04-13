package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ListValue;
import passambler.value.StringValue;

public class FunctionSplit extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ListValue list = new ListValue();

        for (String part : ((StringValue) context.getArgument(0)).getValue().split(((StringValue) context.getArgument(1)).getValue())) {
            list.getValue().add(new StringValue(part));
        }

        return list;
    }
}
