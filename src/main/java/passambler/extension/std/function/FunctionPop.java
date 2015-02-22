package passambler.extension.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
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
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueList list = (ValueList) arguments[0];
        
        Value last = list.get(list.getIndexCount() - 1);
        
        list.remove(list.getIndexCount() - 1);
        
        return last;
    }
}
