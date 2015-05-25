package passambler.module.math.function;

import java.math.BigDecimal;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.NumberValue;

public abstract class SimpleMathFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new NumberValue(getReturnValue(((NumberValue) context.getArgument(0)).getValue()));
    }

    public abstract BigDecimal getReturnValue(BigDecimal value);
}
