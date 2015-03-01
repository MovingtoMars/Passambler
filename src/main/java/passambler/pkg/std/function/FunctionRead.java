package passambler.pkg.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
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
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        return ((ReadHandler) arguments[0]).read();
    }
}
