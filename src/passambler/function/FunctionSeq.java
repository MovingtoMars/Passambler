package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;
import passambler.val.ValArray;
import passambler.val.ValNumber;

public class FunctionSeq implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        if (argument == 0 || argument == 1) {
            return value instanceof ValNumber;
        }

        return false;
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        int min = 0, max;

        if (arguments.length == 1) {
            max = ((ValNumber) arguments[0]).getValueAsInteger();
        } else {
            min = ((ValNumber) arguments[0]).getValueAsInteger();
            max = ((ValNumber) arguments[1]).getValueAsInteger();
        }

        if (min > max) {
            throw new ParserException("%d can't be bigger than %d", min, max);
        }

        ValArray sequence = new ValArray(max - min + 1);

        int i = 0;

        for (int current = min; current <= max; ++current) {
            sequence.setIndex(i, new ValNumber(current));

            i++;
        }

        return sequence;
    }
}
