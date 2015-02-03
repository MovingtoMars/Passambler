package passambler.function;

import passambler.parser.Parser;
import passambler.val.Val;
import passambler.val.ValArray;
import passambler.val.ValNumber;

public class FunctionArray implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        return value instanceof ValNumber;
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) {
        return new ValArray(((ValNumber) arguments[0]).getValueAsInteger());
    }
}
