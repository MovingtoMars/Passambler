package passambler.value.function;

import passambler.parser.ErrorException;
import passambler.parser.ParserException;
import passambler.value.Value;

public class FunctionThrow extends Value implements Function {
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
        throw new ErrorException(context.getArgument(0));
    }
}