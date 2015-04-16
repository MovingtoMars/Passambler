package passambler.bundle.math.function;

import java.util.Random;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ErrorValue;
import passambler.value.NumberValue;

public class RandFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof NumberValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        int min = ((NumberValue) context.getArgument(0)).getValue().intValue();
        int max = ((NumberValue) context.getArgument(1)).getValue().intValue();

        if (min > max) {
            throw new ErrorException(new ErrorValue("Invalid bounds (%d > %d)", min, max));
        }

        return new NumberValue(new Random().nextInt((max - min) + 1) + min);
    }
}
