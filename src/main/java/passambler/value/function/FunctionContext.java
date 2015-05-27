package passambler.value.function;

import passambler.parser.Parser;
import passambler.value.Value;

public class FunctionContext {
    private Parser parser;

    private Value[] arguments;

    public FunctionContext(Parser parser) {
        this(parser, new Value[] {});
    }

    public FunctionContext(Parser parser, Value[] arguments) {
        this.parser = parser;
        this.arguments = arguments;
    }

    public Parser getParser() {
        return parser;
    }

    public Value[] getArguments() {
        return arguments;
    }

    public Value getArgument(int i) {
        return arguments[i];
    }
}
