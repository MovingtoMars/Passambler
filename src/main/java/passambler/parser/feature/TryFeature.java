package passambler.parser.feature;

import passambler.exception.EngineException;
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

        tokens.match(TokenType.LEFT_PAREN);
        tokens.next();

        tokens.match(TokenType.IDENTIFIER);
        String name = tokens.current().getValue();

        tokens.next();
        tokens.match(TokenType.RIGHT_PAREN);

        tokens.next();

        tryBlock.getParser().setCatch(parser.parseBlock(tokens), name);

        Value result = tryBlock.invoke();

        if (result != null && result != Value.VALUE_NIL) {
            return result;
        }

        return null;
    }
}
