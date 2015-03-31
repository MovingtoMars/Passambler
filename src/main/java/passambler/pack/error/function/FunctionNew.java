package passambler.pack.error.function;

import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.pack.error.value.ValueError;
import passambler.value.ValueStr;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class FunctionNew extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return new ValueError(((ValueStr) context.getArgument(0)).getValue());
    }
}