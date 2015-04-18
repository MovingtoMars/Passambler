package passambler.bundle.regex.function;

import java.util.regex.Pattern;
import passambler.bundle.regex.value.PatternValue;
import passambler.exception.EngineException;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class PatternFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new PatternValue(Pattern.compile(((StringValue) context.getArgument(0)).getValue()));
    }
}
