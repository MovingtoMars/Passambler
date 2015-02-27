package passambler.pkg.net.function;

import java.io.IOException;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.pkg.net.value.ValueServerSocket;
import passambler.pkg.net.value.ValueSocket;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionClose extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueSocket || value instanceof ValueServerSocket;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        try {
            if (arguments[0] instanceof ValueSocket) {
                ((ValueSocket) arguments[0]).getValue().close();
            } else if (arguments[0] instanceof ValueServerSocket) {
                ((ValueServerSocket) arguments[0]).getValue().close();
            }
            
            return new ValueBool(true);
        } catch (IOException e) {
            return new ValueBool(false);
        }
    }
}