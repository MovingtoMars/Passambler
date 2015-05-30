package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class FormatFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof StringValue;
        }

        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Object[] data = new Object[context.getArguments().length - 1]; // Don't count the first format argument

        for (int i = 1; i < context.getArguments().length; ++i) {
            data[i - 1] = context.getArgument(i).getValue();
        }

        return new StringValue(String.format(((StringValue) context.getArgument(0)).toString(), data));
    }
}
