package passambler.pack.std.function;

import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueStr;

public class FunctionJoin extends Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueList;
        }

        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        StringBuilder builder = new StringBuilder();

        ValueList list = (ValueList) context.getArgument(0);

        for (Value value : list.getValue()) {
            builder.append(value.toString());

            if (value != list.getValue().get(list.getValue().size() - 1)) {
                builder.append(((ValueStr) context.getArgument(1)).getValue());
            }
        }

        return new ValueStr(builder.toString());
    }
}
