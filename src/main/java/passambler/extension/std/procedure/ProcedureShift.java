package passambler.extension.std.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueList;

public class ProcedureShift extends Procedure {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueList;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueList list = (ValueList) arguments[0];
        
        Value first = list.get(0);
        
        list.remove(0);
        
        return first;
    }
}
