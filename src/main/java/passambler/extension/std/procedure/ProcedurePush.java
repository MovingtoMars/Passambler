package passambler.extension.std.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueList;

public class ProcedurePush implements Procedure {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueList;
        } else if (argument == 1) {
            return true;
        }
        
        return false;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ((ValueList) arguments[0]).add(arguments[1]);
        
        return null;
    }
}
