package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.DictValue;
import passambler.value.ListValue;

public class EntriesFunction extends Value implements Function {
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

        ((DictValue) context.getArgument(0)).getValue().entrySet().stream().forEach((set) -> {
            Value value = new Value();

            value.setProperty("key", set.getKey());
            value.setProperty("value", set.getValue());

            list.getValue().add(value);
        });

        return list;
    }
}
