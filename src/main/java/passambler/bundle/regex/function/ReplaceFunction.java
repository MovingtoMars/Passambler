package passambler.bundle.regex.function;

import passambler.exception.EngineException;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class ReplaceFunction extends Value implements Function {
    private boolean all;

    public ReplaceFunction(boolean all) {
        this.all = all;
    }

    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        String input = ((StringValue) context.getArgument(0)).toString();
        String regex = ((StringValue) context.getArgument(1)).toString();
        String replacement = ((StringValue) context.getArgument(2)).toString();

        return new StringValue(all ? input.replaceAll(regex, replacement) : input.replace(regex, replacement));
    }
}
