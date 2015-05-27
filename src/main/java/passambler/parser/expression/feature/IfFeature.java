package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class IfFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.IF;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        return new passambler.parser.feature.IfFeature().perform(parser.getParser(), parser.getTokens());
    }
}
