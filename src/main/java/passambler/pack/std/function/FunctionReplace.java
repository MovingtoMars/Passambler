package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionReplace extends Function {
    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return new ValueStr(((ValueStr) context.getArgument(0)).getValue().replace(((ValueStr) context.getArgument(1)).getValue(), ((ValueStr) context.getArgument(2)).getValue()));
    }
}
