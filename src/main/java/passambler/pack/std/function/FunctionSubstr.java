package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class FunctionSubstr extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueStr;
        }

        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ValueStr str = (ValueStr) context.getArgument(0);
        int min = ((ValueNum) context.getArgument(1)).getValue().intValue();

        if (context.getArguments().length == 3) {
            int max = ((ValueNum) context.getArgument(2)).getValue().intValue();

            return new ValueStr(str.getValue().substring(min, max));
        } else {
            return new ValueStr(str.getValue().substring(min));
        }
    }
}
