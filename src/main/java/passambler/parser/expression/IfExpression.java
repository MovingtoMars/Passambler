package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.lexer.TokenType;
import passambler.parser.statement.IfStatement;
import passambler.value.Value;

public class IfExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.IF;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        return new IfStatement().perform(parser.getParser(), parser.getTokens());
    }
}
