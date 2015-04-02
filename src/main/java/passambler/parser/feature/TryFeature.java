package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.Value;

public class TryFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return stream.first().getType() == TokenType.TRY;
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        stream.next();

        Block tryBlock = parser.block(stream);

        stream.next();

        stream.match(TokenType.CATCH);
        stream.next();

        stream.match(TokenType.LPAREN);
        stream.next();

        stream.match(TokenType.IDENTIFIER);
        String name = stream.current().getValue();

        stream.next();
        stream.match(TokenType.RPAREN);

        stream.next();

        tryBlock.getParser().setCatch(parser.block(stream), name);

        Value result = tryBlock.invoke();

        return result;
    }
}