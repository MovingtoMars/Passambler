package passambler.pack.net.function;

import java.io.IOException;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.pack.net.value.ValueServerSocket;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionListen extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        try {
            return new ValueServerSocket(((ValueNum) arguments[0]).getValue().intValue());
        } catch (IOException e) {
            return new ValueBool(false);
        }
    }
}
