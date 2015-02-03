package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;
import passambler.val.ValNumber;

public class FunctionSqrt implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        return value instanceof ValNumber;
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        return new ValNumber(Math.sqrt(((ValNumber) arguments[0]).getValue()));
    }
}
