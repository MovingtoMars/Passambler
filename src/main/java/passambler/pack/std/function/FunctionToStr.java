package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionToStr extends Value implements Function {
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
        return new ValueStr(context.getArgument(0).toString());
    }
}
