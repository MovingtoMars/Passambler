package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ReadHandler;

public class FunctionRead extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ReadHandler;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return ((ReadHandler) context.getArgument(0)).read();
    }
}
