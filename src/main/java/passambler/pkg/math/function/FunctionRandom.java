package passambler.pkg.math.function;

import java.util.Random;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionRandom extends Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        int min = ((ValueNum) arguments[0]).getValue().intValue();
        int max = ((ValueNum) arguments[1]).getValue().intValue();

        if (min > max) {
            throw new ParserException(ParserException.Type.BAD_SYNTAX, String.format("%d can't be bigger than %d", min, max));
        }

        return new ValueNum(new Random().nextInt((max - min) + 1) + min);
    }
}
