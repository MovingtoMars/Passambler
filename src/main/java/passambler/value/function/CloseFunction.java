package passambler.value.function;

import passambler.exception.EngineException;
import passambler.value.CloseableValue;
import passambler.value.Value;

public class CloseFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof CloseableValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ((CloseableValue) context.getArgument(0)).close();

        return null;
    }
}
