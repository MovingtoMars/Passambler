package passambler.bundle.std.function;

import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.ListValue;
import passambler.value.StringValue;

public class JoinFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ListValue;
        }

        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        StringBuilder builder = new StringBuilder();

        ListValue list = (ListValue) context.getArgument(0);

        for (Value value : list.getValue()) {
            builder.append(value.toString());

            if (value != list.getValue().get(list.getValue().size() - 1)) {
                builder.append(((StringValue) context.getArgument(1)).getValue());
            }
        }

        return new StringValue(builder.toString());
    }
}
