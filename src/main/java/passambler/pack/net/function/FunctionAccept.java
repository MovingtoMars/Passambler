package passambler.pack.net.function;

import java.io.IOException;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.pack.net.value.ValueServerSocket;
import passambler.pack.net.value.ValueSocket;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionAccept extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueServerSocket;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueServerSocket socket = (ValueServerSocket) arguments[0];

        try {
            return new ValueSocket(socket.getValue().accept());
        } catch (IOException e) {
            return new ValueBool(false);
        }
    }
}
