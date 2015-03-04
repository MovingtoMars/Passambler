package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueList;

public class FunctionReverse extends Function {
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
        ValueList value = (ValueList) context.getArgument(0);

        ValueList subList = new ValueList();

        for (int i = value.getValue().size() - 1; i >= 0; --i) {
            subList.getValue().add(value.getValue().get(i));
        }

        return subList;
    }
}
