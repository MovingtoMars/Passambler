package passambler.pack.net.function;

import java.io.IOException;
import passambler.parser.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.pack.net.value.ValueServerSocket;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionListen extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        try {
            return new ValueServerSocket(((ValueNum) context.getArgument(0)).getValue().intValue());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
