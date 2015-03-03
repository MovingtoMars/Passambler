package passambler.pack.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionBool extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        try {
            return new ValueBool(Boolean.valueOf(arguments[0].toString()));
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
