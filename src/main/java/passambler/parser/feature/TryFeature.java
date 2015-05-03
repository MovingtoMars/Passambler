package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.Value;

public class TryFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.get(0).getType() == TokenType.TRY;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        boolean mayCatch = true, mayFinally = true;

        tokens.next();

        Block tryBlock = parser.parseBlock(tokens);

        tokens.next();

        tokens.match(TokenType.CATCH);
        tokens.next();

        tokens.match(TokenType.IDENTIFIER);
        String name = tokens.current().getValue();

        tokens.next();
        if (tokens.current().getType() == TokenType.IF) {
            tokens.next();

            mayCatch = parser.parseBooleanExpression(tokens, TokenType.LEFT_BRACE).getValue();
        }

        Block catchBlock = parser.parseBlock(tokens);

        Block finallyBlock = null;

        if (tokens.peek() != null && tokens.peek().getType() == TokenType.FINALLY) {
            tokens.next();
            tokens.next();

            if (tokens.current().getType() == TokenType.IF) {
                tokens.next();

                mayFinally = parser.parseBooleanExpression(tokens, TokenType.LEFT_BRACE).getValue();
            }

            finallyBlock = parser.parseBlock(tokens);
        }

        try {
            Value result = tryBlock.invoke();

            if (result != null) {
                return result;
            }
        } catch (ErrorException e) {
            if (mayCatch) {
                catchBlock.getParser().getScope().setSymbol(name, e.getError());

                Value result = catchBlock.invoke();

                if (result != null) {
                    return result;
                }
            } else {
                return e.getError();
            }
        }

        return finallyBlock != null && mayFinally ? finallyBlock.invoke() : null;
    }
}
