package passambler.pack.error.function;

import passambler.pack.error.value.ValueError;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class FunctionIsError extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return new ValueBool(context.getArgument(0) instanceof ValueError);
    }
}