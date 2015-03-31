package passambler.pack.net.function;

import java.io.IOException;
import passambler.value.ValueError;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.pack.net.value.ValueServerSocket;
import passambler.pack.net.value.ValueSocket;
import passambler.value.Value;
import passambler.value.ValueBool;

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
    public Value invoke(FunctionContext context) throws ParserException {
        try {
            if (context.getArgument(0) instanceof ValueSocket) {
                ((ValueSocket) context.getArgument(0)).getValue().close();
            } else if (context.getArgument(1) instanceof ValueServerSocket) {
                ((ValueServerSocket) context.getArgument(1)).getValue().close();
            }

            return new ValueBool(true);
        } catch (IOException e) {
            return new ValueError(e);
        }
    }
}
