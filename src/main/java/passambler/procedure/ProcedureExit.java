package passambler.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueNum;

public class ProcedureExit implements Procedure {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        System.exit(((ValueNum) arguments[0]).getValueAsInteger());

        return null;
    }
}