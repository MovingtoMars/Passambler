package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.lexer.Token;
import passambler.value.Value;

public class UnaryExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType().isUnaryOperator();
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        Token unary = parser.getTokens().current();

        Value value = parser.getParser().parseExpression(parser.getTokens());

        return value.onOperator(value, unary);
    }
}
