package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.parser.ExpressionParser;
import passambler.parser.Parser;
import passambler.value.Value;

public class ExpressionFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return true;
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        new ExpressionParser(parser, stream).parse();

        return null;
    }
}
