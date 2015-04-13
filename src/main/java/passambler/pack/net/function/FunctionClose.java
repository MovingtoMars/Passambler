package passambler.pack.net.function;

import java.io.IOException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.pack.net.value.ValueServerSocket;
import passambler.pack.net.value.ValueSocket;
import passambler.exception.EngineException;
import passambler.value.Value;

public class FunctionClose extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueSocket || value instanceof ValueServerSocket;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            if (context.getArgument(0) instanceof ValueSocket) {
                ((ValueSocket) context.getArgument(0)).getValue().close();
            } else if (context.getArgument(1) instanceof ValueServerSocket) {
                ((ValueServerSocket) context.getArgument(1)).getValue().close();
            }
        } catch (IOException e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
