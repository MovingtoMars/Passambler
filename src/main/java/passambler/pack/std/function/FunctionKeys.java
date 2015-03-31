package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueList;

public class FunctionKeys extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueDict;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        ValueList list = new ValueList();

        ((ValueDict) context.getArgument(0)).getValue().keySet().stream().forEach((key) -> {
            list.getValue().add(key);
        });

        return list;
    }
}
