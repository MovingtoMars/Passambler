package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class PropertyAccessExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.PERIOD;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getTokens().next();

        parser.getTokens().match(TokenType.IDENTIFIER);

        String propertyName = parser.getTokens().current().getValue();

        if (!currentValue.hasProperty(propertyName)) {
            throw new ParserException(ParserExceptionType.UNDEFINED_PROPERTY, parser.getTokens().current().getPosition(), propertyName);
        }

        return currentValue.getProperty(propertyName).getValue();
    }
}
