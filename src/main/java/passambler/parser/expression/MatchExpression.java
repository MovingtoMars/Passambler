package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.lexer.TokenType;
import passambler.parser.statement.MatchStatement;
import passambler.util.ValueConstants;
import passambler.value.Value;

public class MatchExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.MATCH;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        Value result = new MatchStatement().perform(parser.getParser(), parser.getTokens());

        return result != null ? result : ValueConstants.NIL;
    }
}
