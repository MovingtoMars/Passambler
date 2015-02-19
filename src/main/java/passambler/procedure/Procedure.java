package passambler.procedure;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public interface Procedure {
    public int getArguments();

    public boolean isArgumentValid(Value value, int argument);

    public Value invoke(Parser parser, Value... arguments) throws ParserException;
}
