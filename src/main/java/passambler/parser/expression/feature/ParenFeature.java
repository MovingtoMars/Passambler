package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class ParenFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.LEFT_PAREN;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getStream().next();

        return parser.createParser(new TokenStream(parser.getParser().parseExpressionTokens(parser.getStream(), TokenType.RIGHT_PAREN))).parse();
    }
}
