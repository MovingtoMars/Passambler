package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.val.Val;

public interface Function {
    public int getArguments();

    public boolean isArgumentValid(Val value, int argument);

    public Val invoke(Parser parser, Val... arguments) throws ParserException;
}
