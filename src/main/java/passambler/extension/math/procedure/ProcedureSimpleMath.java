package passambler.extension.math.procedure;

import java.math.BigDecimal;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueNum;

public abstract class ProcedureSimpleMath extends Procedure {
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
        return new ValueNum(getValue(((ValueNum) arguments[0]).getValue()));
    }

    public abstract BigDecimal getValue(BigDecimal value);
}
