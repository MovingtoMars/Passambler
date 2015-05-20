package passambler.bundle.std.function;

import passambler.exception.EngineException;
import passambler.util.ValueConstants;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.BooleanValue;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.StringValue;

public class ContainsFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ListValue || value instanceof DictValue || value instanceof StringValue;
        }

        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Value value = context.getArgument(0);

        if (value instanceof StringValue) {
            return new BooleanValue(((StringValue) value).toString().contains(context.getArgument(1).toString()));
        } else if (value instanceof ListValue) {
            return new BooleanValue(((ListValue) value).getValue().contains(context.getArgument(1)));
        } else if (value instanceof DictValue) {
            for (Value entry : ((DictValue) value).getValue().values()) {
                if (entry.equals(context.getArgument(1))) {
                    return ValueConstants.TRUE;
                }
            }
            return ValueConstants.FALSE;
        }

        return null;
    }
}
