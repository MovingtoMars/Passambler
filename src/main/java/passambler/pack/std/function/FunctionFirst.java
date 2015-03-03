package passambler.pack.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.ValueList;
import passambler.value.Value;

public class FunctionFirst extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueList;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        return ((ValueList) arguments[0]).getValue().get(0);
    }
}
