package passambler.pack.net.function;

import java.io.IOException;
import passambler.parser.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.pack.net.value.ValueServerSocket;
import passambler.pack.net.value.ValueSocket;
import passambler.value.Value;

public class FunctionAccept extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueServerSocket;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        ValueServerSocket socket = (ValueServerSocket) context.getArgument(1);

        try {
            return new ValueSocket(socket.getValue().accept());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
