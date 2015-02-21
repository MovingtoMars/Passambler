package passambler.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public abstract class Procedure extends Value {
    public abstract int getArguments();

    public abstract boolean isArgumentValid(Value value, int argument);

    public abstract Value invoke(Parser parser, Value... arguments) throws ParserException;
}
