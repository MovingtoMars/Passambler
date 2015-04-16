package passambler.bundle.net.function;

import java.io.IOException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.bundle.net.value.ServerSocketValue;
import passambler.bundle.net.value.SocketValue;
import passambler.exception.EngineException;
import passambler.value.Value;

public class CloseFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof SocketValue || value instanceof ServerSocketValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            if (context.getArgument(0) instanceof SocketValue) {
                ((SocketValue) context.getArgument(0)).getValue().close();
            } else if (context.getArgument(1) instanceof ServerSocketValue) {
                ((ServerSocketValue) context.getArgument(1)).getValue().close();
            }
        } catch (IOException e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
