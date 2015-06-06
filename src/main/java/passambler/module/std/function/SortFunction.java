package passambler.module.std.function;

import java.util.Collections;
import java.util.Comparator;
import passambler.exception.EngineException;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.function.UserFunction;

public class SortFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ListValue;
        }

        return value instanceof UserFunction;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Comparator comparator = (Comparator<Value>) (Value v1, Value v2) -> {
            if (context.getArguments().length == 1) {
                return v1.getValue().toString().compareTo(v2.getValue().toString());
            } else {
                UserFunction function = (UserFunction) context.getArgument(1);

                try {
                    return ((NumberValue) function.invoke(new FunctionContext(context.getParser(), new Value[] { v1, v2 }))).getValue().intValue();
                } catch (EngineException e) {
                    return 0;
                }
            }
        };

        Collections.sort(((ListValue) context.getArgument(0)).getValue(), comparator);

        return null;
    }
}
