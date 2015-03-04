package passambler.pack.std.function;

import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionEval extends Function {
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
        try {
            return context.getParser().parse(new Lexer(((ValueStr) context.getArgument(0)).getValue()).scan());
        } catch (LexerException e) {
            throw new RuntimeException(e);
        }
    }
}
