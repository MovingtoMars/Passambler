package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionToBool extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        try {
            return new ValueBool(Boolean.valueOf(context.getArgument(0).toString()));
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
