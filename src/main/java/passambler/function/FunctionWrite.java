package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;

public class FunctionWrite implements Function {
    protected boolean newLine;
    
    public FunctionWrite(boolean newLine) {
        this.newLine = newLine;
    }
    
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Val value, int argument) {
        return true;
    }

    @Override
    public Val invoke(Parser parser, Val... arguments) throws ParserException {
        for (Val argument : arguments) {
            System.out.print(argument);
        }
        
        if (newLine) {
            System.out.println();
        }
        
        return null;
    }
}
