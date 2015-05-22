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
        return tokens.current().getType() == TokenType.TRY;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        TokenList conditionalCatch = null;
        TokenList conditionalFinally = null;

        boolean mayCatch = true;
        boolean mayFinally = true;

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

            conditionalCatch = new TokenList(parser.parseExpressionTokens(tokens, TokenType.LEFT_BRACE));
        }

        Block catchBlock = parser.parseBlock(tokens);

        Block finallyBlock = null;

        if (tokens.peek() != null && tokens.peek().getType() == TokenType.FINALLY) {
            tokens.next();
            tokens.next();

            if (tokens.current().getType() == TokenType.IF) {
                tokens.next();

                conditionalFinally = new TokenList(parser.parseExpressionTokens(tokens, TokenType.LEFT_BRACE));
            }

            finallyBlock = parser.parseBlock(tokens);
        }

        boolean invokedFinallyAlready = false;

        try {
            Value result = tryBlock.invoke();

            if (result != null) {
                return result;
            }
        } catch (ErrorException e) {
            // After invoking the try block, set the mayCatch and mayFinally values.
            if (conditionalCatch != null) {
                mayCatch = parser.parseBooleanExpression(conditionalCatch).getValue();
            }

            if (conditionalFinally != null) {
                mayFinally = parser.parseBooleanExpression(conditionalFinally).getValue();
            }

            if (mayCatch) {
                catchBlock.getParser().getScope().setSymbol(name, e.getError());

                Value result = catchBlock.invoke();

                if (result != null) {
                    return result;
                }
            }

            if (conditionalFinally != null) {
                // We've set the mayFinally value before, but the condition might change after invoking the the catch block.
                // That's why we have to re-assign it.
                // We copy the conditionalFinally value so that the TokenList's position is set back to 0.
                mayFinally = parser.parseBooleanExpression(conditionalFinally.copy()).getValue();
            }

            if (mayFinally && finallyBlock != null) {
                invokedFinallyAlready = true;

                Value result = finallyBlock.invoke();

                if (result != null) {
                    return result;
                }
            }

            // We are not allowed to catch the error, so return it because then it is a uncaught error.
            if (!mayCatch) {
                return e.getError();
            }
        }

        // Maybe nothing has to be catched and a finally condition has been changed!
        // So, we have to reset the condition here as well.
        if (conditionalFinally != null) {
            mayFinally = parser.parseBooleanExpression(conditionalFinally.copy()).getValue();
        }

        return mayFinally && finallyBlock != null && !invokedFinallyAlready ? finallyBlock.invoke() : null;
    }
}
