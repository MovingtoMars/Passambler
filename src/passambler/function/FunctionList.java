package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;
import passambler.val.ValList;

public class FunctionList implements Function {
    @Override
    public int getArguments() {
        return 0;
    }
    
    @Override
    public boolean isArgumentValid(Val val, int argument) {
        return false;
    }
    
    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        return new ValList();
    }
}
