package passambler.pkg.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueStr;
import passambler.value.ValueWriteHandler;

public class FunctionWrite extends Function {
    private ValueWriteHandler handler;
    
    private boolean newLine;

    public FunctionWrite(ValueWriteHandler handler, boolean newLine) {
        this.handler = handler;
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
        ValueWriteHandler handler = this.handler;
        
        if (arguments.length > 0 && arguments[0] instanceof ValueWriteHandler) {
            handler = (ValueWriteHandler) arguments[0];
        }
        
        for (Value argument : arguments) {
            if (argument == arguments[0] && argument instanceof ValueWriteHandler) {
                continue;
            }

            handler.write(argument);
            
            if (argument != arguments[arguments.length - 1]) {
                handler.write(new ValueStr(" "));
            }
        }

        if (newLine) {
            handler.write(new ValueStr("\n"));
        }

        return null;
    }
}
