package passambler.pack.file.function;

import java.nio.file.Path;
import java.nio.file.Paths;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueStr;

public abstract class FunctionSimpleFile extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return getValue(Paths.get(((ValueStr) context.getArgument(0)).getValue()));
    }

    public abstract Value getValue(Path file);
}
