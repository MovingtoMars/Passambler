package passambler.extension.std.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueStr;

public class ProcedureStr extends Procedure {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        return new ValueStr(arguments[0].toString());
    }
}