package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.BooleanValue;
import passambler.value.Value;

public class TernaryFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.TERNARY && currentValue instanceof BooleanValue;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenList tokens = parser.getTokens();

        tokens.next();

        Value ifTrue = parser.getParser().parseExpression(tokens, TokenType.COL);

        tokens.match(TokenType.COL);
        tokens.next();

        Value ifFalse = parser.getParser().parseExpression(tokens);

        return ((BooleanValue) currentValue).getValue() ? ifTrue : ifFalse;
    }
}
