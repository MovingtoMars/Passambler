package passambler.pack.std.function;

import java.math.BigDecimal;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionToNum extends Value implements Function {
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
            return new ValueNum(new BigDecimal(context.getArgument(0).toString()));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
