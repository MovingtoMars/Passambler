package passambler.pack.net.http.function;

import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.pack.net.http.value.ValueFileHandler;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionFileHandler extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        return new ValueFileHandler(((ValueStr) arguments[0]).getValue());
    }
}
