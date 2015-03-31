package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueNum;

public class FunctionSize extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueList;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return new ValueNum(((ValueList) context.getArgument(0)).getValue().size());
    }
}
