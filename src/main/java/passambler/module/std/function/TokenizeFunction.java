package passambler.module.std.function;

import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.exception.LexerException;
import passambler.lexer.Lexer;
import passambler.lexer.Token;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class TokenizeFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        Lexer lexer = new Lexer(((StringValue) context.getArgument(0)).toString());

        ListValue list = new ListValue();

        try {
            for (Token token : lexer.tokenize()) {
                DictValue element = new DictValue();

                element.setEntry(new StringValue("name"), new StringValue(token.getType().toString()));
                element.setEntry(new StringValue("value"), new StringValue(token.getValue()));

                list.getValue().add(element);
            }
        } catch (LexerException e) {
            throw new ErrorException(e);
        }

        return list;
    }
}
