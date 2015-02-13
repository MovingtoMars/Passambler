package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

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
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        for (Value argument : arguments) {
            System.out.print(argument);
        }
        
        if (newLine) {
            System.out.println();
        }
        
        return null;
    }
}
