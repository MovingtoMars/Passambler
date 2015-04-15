package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.ListValue;
import passambler.value.Value;

public class ListFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.LEFT_BRACKET;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        ListValue list = new ListValue();

        TokenStream stream = parser.getStream();

        stream.next();

        while (stream.hasNext()) {
            // Empty lists
            if (stream.current().getType() == TokenType.RIGHT_BRACKET) {
                break;
            }

            Value value = parser.getParser().parseExpression(stream, TokenType.COMMA, TokenType.RIGHT_BRACKET);

            list.getValue().add(value);

            stream.match(TokenType.COMMA, TokenType.RIGHT_BRACKET);

            if (stream.current().getType() == TokenType.RIGHT_BRACKET) {
                break;
            } else {
                stream.next();
            }
        }

        return list;
    }
}
