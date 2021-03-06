package passambler.module.std.function;

import java.math.BigDecimal;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.NumberValue;

public class ToNumFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            return new NumberValue(new BigDecimal(context.getArgument(0).toString()));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
