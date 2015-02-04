package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;
import passambler.val.ValString;

public class FunctionStr implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        return true;
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        return new ValString(arguments[0].toString());
    }
}
