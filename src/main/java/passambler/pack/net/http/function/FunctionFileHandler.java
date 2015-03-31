package passambler.pack.net.http.function;

import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.pack.net.http.value.ValueFileHandler;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionFileHandler extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return new ValueFileHandler(((ValueStr) context.getArgument(0)).getValue());
    }
}
