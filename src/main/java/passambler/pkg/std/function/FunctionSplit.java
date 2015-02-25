package passambler.pkg.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueStr;

public class FunctionSplit extends Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueList list = new ValueList();

        for (String part : ((ValueStr) arguments[0]).getValue().split(((ValueStr) arguments[1]).getValue())) {
            list.getValue().add(new ValueStr(part));
        }

        return list;
    }
}
