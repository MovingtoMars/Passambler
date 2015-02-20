package passambler.extension.str.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class ProcedureIndexOf implements Procedure {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        return new ValueNum(((ValueStr) arguments[0]).getValue().indexOf(((ValueStr) arguments[1]).getValue()));
    }
}
