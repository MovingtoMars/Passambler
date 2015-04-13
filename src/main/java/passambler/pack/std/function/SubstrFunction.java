package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.NumberValue;
import passambler.value.StringValue;

public class SubstrFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof StringValue;
        }

        return value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        StringValue str = (StringValue) context.getArgument(0);
        int min = ((NumberValue) context.getArgument(1)).getValue().intValue();

        if (context.getArguments().length == 3) {
            int max = ((NumberValue) context.getArgument(2)).getValue().intValue();

            return new StringValue(str.getValue().substring(min, max));
        } else {
            return new StringValue(str.getValue().substring(min));
        }
    }
}
