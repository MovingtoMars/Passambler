package passambler.pack.std.function;

import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.lexer.Lexer;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionEval extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return context.getParser().parse(new Lexer(((ValueStr) context.getArgument(0)).getValue()).scan());
    }
}
