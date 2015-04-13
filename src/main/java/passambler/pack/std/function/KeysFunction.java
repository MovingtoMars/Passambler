package passambler.pack.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.DictValue;
import passambler.value.ListValue;

public class KeysFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof DictValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ListValue list = new ListValue();

        ((DictValue) context.getArgument(0)).getValue().keySet().stream().forEach((key) -> {
            list.getValue().add(key);
        });

        return list;
    }
}
