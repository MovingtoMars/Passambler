package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.value.BooleanValue;
import passambler.value.Value;

public class TernaryExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.TERNARY && currentValue instanceof BooleanValue;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenList tokens = parser.getTokens();

        tokens.next();

        TokenList ifTrue = new TokenList(parser.getParser().parseExpressionTokens(tokens, TokenType.EXCLUSIVE_SLICE));

        tokens.match(TokenType.EXCLUSIVE_SLICE);
        tokens.next();

        TokenList ifFalse = new TokenList(parser.getParser().parseExpressionTokens(tokens));

        return ((BooleanValue) currentValue).getValue() ? new ExpressionParser(parser.getParser(), ifTrue).parse() : new ExpressionParser(parser.getParser(), ifFalse).parse();
    }
}
