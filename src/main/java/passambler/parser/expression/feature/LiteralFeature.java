package passambler.parser.expression.feature;

import java.math.BigDecimal;
import passambler.exception.EngineException;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class LiteralFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.NUMBER || parser.getTokens().current().getType() == TokenType.STRING;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenList tokens = parser.getTokens();

        Token token = tokens.current();

        switch (token.getType()) {
            case STRING:
                return new StringValue(token.getValue());
            case NUMBER:
                StringBuilder number = new StringBuilder();

                number.append(token.getValue());

                if (tokens.peek() != null && tokens.peek().getType() == TokenType.PERIOD) {
                    tokens.next();
                    tokens.next();

                    tokens.match(TokenType.NUMBER);

                    number.append(".").append(tokens.current().getValue());
                }

                return new NumberValue(new BigDecimal(number.toString()));
            default:
                return null;
        }
    }
}
