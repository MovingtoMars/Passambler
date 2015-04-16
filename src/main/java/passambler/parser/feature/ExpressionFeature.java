package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.value.Value;

public class ExpressionFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return true;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        new ExpressionParser(parser, tokens).parse();

        return null;
    }
}
