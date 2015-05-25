package passambler.module.net.function;

import java.io.IOException;
import java.net.Socket;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.module.net.value.SocketValue;
import passambler.value.Value;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class OpenFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof StringValue;
        } else if (argument == 1) {
            return value instanceof NumberValue;
        }

        return false;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            return new SocketValue(new Socket(((StringValue) context.getArgument(0)).toString(), ((NumberValue) context.getArgument(1)).getValue().intValue()));
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
