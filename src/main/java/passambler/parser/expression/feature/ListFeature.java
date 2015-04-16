package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.ListValue;
import passambler.value.Value;

public class ListFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.LEFT_BRACKET;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        ListValue list = new ListValue();

        TokenList tokens = parser.getTokens();

        tokens.next();

        while (tokens.hasNext()) {
            // Empty lists
            if (tokens.current().getType() == TokenType.RIGHT_BRACKET) {
                break;
            }

            Value value = parser.getParser().parseExpression(tokens, TokenType.COMMA, TokenType.RIGHT_BRACKET);

            list.getValue().add(value);

            tokens.match(TokenType.COMMA, TokenType.RIGHT_BRACKET);

            if (tokens.current().getType() == TokenType.RIGHT_BRACKET) {
                break;
            } else {
                tokens.next();
            }
        }

        return list;
    }
}
