package passambler.value.function;

import passambler.parser.ParserException;
import passambler.value.Value;

public interface Function {
    public int getArguments();

    public boolean isArgumentValid(Value value, int argument);

    public Value invoke(FunctionContext context) throws ParserException;
}
