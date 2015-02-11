package passambler.function;

import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public interface Function {
    public static FunctionExit FUNCTION_EXIT = new FunctionExit();
    public static FunctionRandom FUNCTION_RANDOM = new FunctionRandom();
    public static FunctionSqrt FUNCTION_SQRT = new FunctionSqrt();
    public static FunctionWrite FUNCTION_WRITE = new FunctionWrite(false);
    public static FunctionWrite FUNCTION_WRITELN = new FunctionWrite(true);
    
    public int getArguments();

    public boolean isArgumentValid(Value value, int argument);

    public Value invoke(Parser parser, Value... arguments) throws ParserException;
}
