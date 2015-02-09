package passambler.function;

import java.util.Random;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionRandom implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0 || argument == 1) {
            return value instanceof ValueNum;
        }

        return false;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        int min = 0, max;

        if (arguments.length == 1) {
            max = ((ValueNum) arguments[0]).getValueAsInteger();
        } else {
            min = ((ValueNum) arguments[0]).getValueAsInteger();
            max = ((ValueNum) arguments[1]).getValueAsInteger();
        }

        if (min > max) {
            throw new ParserException(ParserException.Type.BAD_SYNTAX, String.format("%d can't be bigger than %d", min, max));
        }

        return new ValueNum(new Random().nextInt((max - min) + 1) + min);
    }
}
