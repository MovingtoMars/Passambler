package passambler.extension.std.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.function.Function;
import passambler.value.IndexedValue;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionEmpty extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof IndexedValue;
    }

    @Override
    public Value invoke(Parser parser, Value... arguments) throws ParserException {    
        return new ValueBool(((IndexedValue) arguments[0]).getIndexCount() == 0);
    }
}
