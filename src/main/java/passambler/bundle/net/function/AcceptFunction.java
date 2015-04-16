package passambler.bundle.net.function;

import java.io.IOException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.bundle.net.value.ServerSocketValue;
import passambler.bundle.net.value.SocketValue;
import passambler.exception.EngineException;
import passambler.value.Value;

public class AcceptFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ServerSocketValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ServerSocketValue socket = (ServerSocketValue) context.getArgument(0);

        try {
            return new SocketValue(socket.getValue().accept());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
