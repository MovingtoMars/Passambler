package passambler.function;

import passambler.parser.ParserException;
import passambler.value.Value;

public abstract class FunctionSimple extends Function {
    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return false;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return getValue();
    }

    public abstract Value getValue();
}
