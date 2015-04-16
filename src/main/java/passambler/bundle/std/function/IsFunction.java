package passambler.bundle.std.function;

import passambler.exception.EngineException;
import passambler.value.BooleanValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class IsFunction extends Value implements Function {
    private Class<? extends Value> classMatch;

    public IsFunction(Class<? extends Value> classMatch) {
        this.classMatch = classMatch;
    }

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
        return new BooleanValue(context.getArgument(0).getClass().equals(classMatch));
    }
}
