package passambler.extension.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionReplace extends Function {
    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        return new ValueStr(((ValueStr) arguments[0]).getValue().replace(((ValueStr) arguments[1]).getValue(), ((ValueStr) arguments[2]).getValue()));
    }
}
