package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;
import passambler.val.ValNum;

public class FunctionSqrt implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        return value instanceof ValNum;
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        return new ValNum(Math.sqrt(((ValNum) arguments[0]).getValue()));
    }
}
