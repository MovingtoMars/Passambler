package passambler.pack.std.function;

import passambler.parser.ParserException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueStr;

public class FunctionSplit extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        ValueList list = new ValueList();

        for (String part : ((ValueStr) context.getArgument(0)).getValue().split(((ValueStr) context.getArgument(1)).getValue())) {
            list.getValue().add(new ValueStr(part));
        }

        return list;
    }
}
