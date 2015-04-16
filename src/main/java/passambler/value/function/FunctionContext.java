package passambler.value.function;

import passambler.parser.Parser;
import passambler.value.Value;

public class FunctionContext {
    private Parser parser;

    private Value[] arguments;

    private boolean assignment;

    public FunctionContext(Parser parser) {
        this(parser, new Value[] {});
    }

    public FunctionContext(Parser parser, Value[] arguments) {
        this(parser, arguments, false);
    }

    public FunctionContext(Parser parser, Value[] arguments, boolean assignment) {
        this.parser = parser;
        this.arguments = arguments;
        this.assignment = assignment;
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

    public boolean isAssignment() {
        return assignment;
    }
}
