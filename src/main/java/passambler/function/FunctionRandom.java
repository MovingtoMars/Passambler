package passambler.function;

import java.util.Random;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;
import passambler.val.ValNum;

public class FunctionRandom implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        if (argument == 0 || argument == 1) {
            return value instanceof ValNum;
        }

        return false;
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        int min = 0, max;

        if (arguments.length == 1) {
            max = ((ValNum) arguments[0]).getValueAsInteger();
        } else {
            min = ((ValNum) arguments[0]).getValueAsInteger();
            max = ((ValNum) arguments[1]).getValueAsInteger();
        }

        if (min > max) {
            throw new ParserException(ParserException.Type.BAD_SYNTAX, String.format("%d can't be bigger than %d", min, max));
        }

        return new ValNum(new Random().nextInt((max - min) + 1) + min);
    }
}
