package passambler.pack.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueList;

public class FunctionPush extends Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueList;
        }

        return true;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ((ValueList) arguments[0]).getValue().add(arguments[1]);

        return null;
    }
}
