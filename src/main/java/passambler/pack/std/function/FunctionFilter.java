package passambler.pack.std.function;

import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueList;

public class FunctionFilter extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        if (argument == 0) {
            return value instanceof ValueList;
        }

        return value instanceof Function && ((Function) value).getArguments() == 1;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        ValueList list = (ValueList) context.getArgument(0);

        Function callback = (Function) context.getArgument(1);
        
        ValueList filteredList = new ValueList();

        for (int i = 0; i < list.getValue().size(); ++i) {
            Value result = callback.invoke(new FunctionContext(context.getParser(), new Value[] { list.getValue().get(i) }));

            if (!(result instanceof ValueBool)) {
                throw new ParserException(ParserException.Type.EXPECTED_A_BOOL);
            }

            if (((ValueBool) result).getValue() == true) {
                filteredList.getValue().add(list.getValue().get(i));
            }
        }

        return filteredList;
    }
}
