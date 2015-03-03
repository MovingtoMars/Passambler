package passambler.pack.thread.function;

import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionSleep extends Function {
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
            Thread.sleep(((ValueNum) arguments[0]).getValue().intValue());

            return new ValueBool(true);
        } catch (InterruptedException e) {
            return new ValueBool(false);
        }
    }
}
