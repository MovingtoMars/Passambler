package passambler.pkg.thread.function;

import java.util.ArrayList;
import java.util.List;
import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public class FunctionStart extends Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof Function;
        }

        return true;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        new Runnable() {
            @Override
            public void run() {
                try {
                    Function function = (Function) arguments[0];

                    if (function.getArguments() != arguments.length - 1) {
                        throw new ParserException(ParserException.Type.INVALID_ARGUMENT_COUNT, null, function.getArguments(), arguments.length - 1);
                    }

                    List<Value> values = new ArrayList<>();

                    for (int i = 1; i < arguments.length; ++i) {
                        values.add(arguments[i]);
                    }

                    function.invoke(parser, values.toArray(new Value[values.size()]));
                } catch (ParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }.run();

        return null;
    }
}
