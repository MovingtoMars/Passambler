package passambler.pack.std.function;

import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.ListValue;
import passambler.value.StringValue;

public class ListFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ListValue list = new ListValue();

        for (char c : ((StringValue) context.getArgument(0)).getValue().toCharArray()) {
            list.getValue().add(new StringValue(String.valueOf(c)));
        }

        return list;
    }
}
