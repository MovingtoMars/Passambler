package passambler.pack.net.function;

import java.io.IOException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.pack.net.value.ValueServerSocket;
import passambler.pack.net.value.ValueSocket;
import passambler.exception.EngineException;
import passambler.value.Value;

public class AcceptFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueServerSocket;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ValueServerSocket socket = (ValueServerSocket) context.getArgument(0);

        try {
            return new ValueSocket(socket.getValue().accept());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
