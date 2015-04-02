package passambler.pack.math.function;

import java.math.BigDecimal;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueNum;

public abstract class FunctionSimpleMath extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new ValueNum(getReturnValue(((ValueNum) context.getArgument(0)).getValue()));
    }

    public abstract BigDecimal getReturnValue(BigDecimal value);
}
