package passambler.function;

import passambler.parser.ParserException;
import passambler.value.Value;

public abstract class Function extends Value {
    public abstract int getArguments();

    public abstract boolean isArgumentValid(Value value, int argument);

    public abstract Value invoke(FunctionContext context) throws ParserException;
}
