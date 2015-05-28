package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.DictValue;
import passambler.value.Value;

public class DictExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.LEFT_BRACE;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        DictValue dict = new DictValue();

        TokenList tokens = parser.getTokens();

        tokens.next();

        while (tokens.hasNext()) {
            // Empty dicts
            if (tokens.current().getType() == TokenType.RIGHT_BRACE) {
                break;
            }

            Value key = parser.getParser().parseExpression(tokens, TokenType.EXCLUSIVE_SLICE);

            tokens.next();

            Value value = parser.getParser().parseExpression(tokens, TokenType.COMMA, TokenType.RIGHT_BRACE);

            dict.setEntry(key, value);

            tokens.match(TokenType.COMMA, TokenType.RIGHT_BRACE);

            if (tokens.current().getType() == TokenType.RIGHT_BRACE) {
                break;
            } else {
                tokens.next();
            }
        }

        return dict;
    }
}
