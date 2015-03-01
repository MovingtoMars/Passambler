package passambler.pkg.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueList;

public class FunctionEntries extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueDict;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueList list = new ValueList();

        ((ValueDict) arguments[0]).getValue().entrySet().stream().forEach((set) -> {
            Value value = new Value();

            value.setProperty("Key", set.getKey());
            value.setProperty("Value", set.getValue());

            list.getValue().add(value);
        });

        return list;
    }
}
