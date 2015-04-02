package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.ExpressionParser;
import passambler.parser.Parser;
import passambler.value.Value;

public class ReturnFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return stream.first().getType() == TokenType.RETURN;
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        stream.next();

        return new ExpressionParser(parser, stream.copyAtCurrentPosition()).parse();
    }
}
