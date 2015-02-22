package passambler.extension.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueStr;

public class FunctionReverse extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueList || value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        if (arguments[0] instanceof ValueStr) {
            return new ValueStr(new StringBuilder(((ValueStr) arguments[0]).getValue()).reverse().toString());
        }
        
        ValueList value = (ValueList) arguments[0];
        
        ValueList subList = new ValueList();
        
        for (int i = value.getIndexCount() - 1; i >= 0; --i) {
            subList.add(value.get(i));
        }
        
        return subList;
    }
}
