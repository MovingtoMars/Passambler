package passambler.module.regex.function;

import passambler.exception.EngineException;
import passambler.value.BooleanValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class MatchesFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new BooleanValue(((StringValue) context.getArgument(0)).toString().matches(((StringValue) context.getArgument(1)).toString()));
    }
}
