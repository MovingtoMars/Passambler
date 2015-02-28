package passambler.pkg.std.function;

import passambler.function.Function;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueList;
import passambler.value.ValueStr;

public class FunctionList extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        ValueList list = new ValueList();
        
        for (char c : ((ValueStr) arguments[0]).getValue().toCharArray()) {
            list.getValue().add(new ValueStr(String.valueOf(c)));
        }
        
        return list;
    }
}
