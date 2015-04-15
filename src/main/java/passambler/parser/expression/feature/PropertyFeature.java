package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class PropertyFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.PERIOD;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getStream().next();

        parser.getStream().match(TokenType.IDENTIFIER);

        String propertyName = parser.getStream().current().getValue();

        if (!currentValue.hasProperty(propertyName)) {
            throw new ParserException(ParserExceptionType.UNDEFINED_PROPERTY, parser.getStream().current().getPosition(), propertyName);
        }

        return currentValue.getProperty(propertyName).getValue();
    }
}
