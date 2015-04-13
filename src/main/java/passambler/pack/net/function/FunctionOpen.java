package passambler.pack.net.function;

import java.io.IOException;
import java.net.Socket;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.pack.net.value.ValueSocket;
import passambler.value.Value;
import passambler.value.ValueNum;
import passambler.value.ValueStr;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class FunctionOpen extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueStr;
        } else if (argument == 1) {
            return value instanceof ValueNum;
        }

        return false;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            return new ValueSocket(new Socket(((ValueStr) context.getArgument(0)).getValue(), ((ValueNum) context.getArgument(1)).getValue().intValue()));
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }

}