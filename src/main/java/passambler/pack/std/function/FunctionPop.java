package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueList;

public class FunctionPop extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueList;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        ValueList list = (ValueList) context.getArgument(0);

        Value last = list.getValue().get(list.getValue().size() - 1);

        list.getValue().remove(list.getValue().size() - 1);

        return last;
    }
}