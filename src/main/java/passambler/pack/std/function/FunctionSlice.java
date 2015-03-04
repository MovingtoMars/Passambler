package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueNum;

public class FunctionSlice extends Function {
    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueList;
        }

        return value instanceof ValueNum;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        ValueList list = (ValueList) context.getArgument(0);

        ValueList subList = new ValueList();

        for (int i = ((ValueNum) context.getArgument(1)).getValue().intValue(); i <= ((ValueNum) context.getArgument(2)).getValue().intValue(); ++i) {
            subList.getValue().add(list.getValue().get(i));
        }

        return subList;
    }
}
