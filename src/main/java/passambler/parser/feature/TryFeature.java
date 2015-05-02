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
        tokens.next();

        Block tryBlock = parser.parseBlock(tokens);

        tokens.next();

        tokens.match(TokenType.CATCH);
        tokens.next();

        tokens.match(TokenType.IDENTIFIER);
        String name = tokens.current().getValue();

        tokens.next();

        Block catchBlock = parser.parseBlock(tokens);

        try {
            Value result = tryBlock.invoke();

            if (result != null) {
                return result;
            }
        } catch (ErrorException e) {
            catchBlock.getParser().getScope().setSymbol(name, e.getError());

            Value result = catchBlock.invoke();

            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
