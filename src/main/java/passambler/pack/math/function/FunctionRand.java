package passambler.pack.math.function;

import java.util.Random;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionRand extends Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        int min = ((ValueNum) context.getArgument(0)).getValue().intValue();
        int max = ((ValueNum) context.getArgument(1)).getValue().intValue();

        if (min > max) {
            throw new ParserException(ParserException.Type.BAD_SYNTAX, String.format("%d can't be bigger than %d", min, max));
        }

        return new ValueNum(new Random().nextInt((max - min) + 1) + min);
    }
}
