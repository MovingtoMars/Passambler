package passambler.pack.math.function;

import java.math.BigDecimal;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueNum;

public abstract class FunctionSimpleMath extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return new ValueNum(getValue(((ValueNum) context.getArgument(0)).getValue()));
    }

    public abstract BigDecimal getValue(BigDecimal value);
}