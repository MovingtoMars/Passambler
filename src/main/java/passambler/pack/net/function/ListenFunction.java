package passambler.pack.net.function;

import java.io.IOException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.pack.net.value.ValueServerSocket;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.NumberValue;

public class ListenFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            return new ValueServerSocket(((NumberValue) context.getArgument(0)).getValue().intValue());
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
