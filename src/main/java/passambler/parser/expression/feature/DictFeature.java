package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.DictValue;
import passambler.value.Value;

public class DictFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.LEFT_BRACE;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        DictValue dict = new DictValue();

        TokenStream stream = parser.getStream();

        stream.next();

        while (stream.hasNext()) {
            Value key = parser.getParser().parseExpression(stream, TokenType.COL);
            
            stream.next();
            
            Value value = parser.getParser().parseExpression(stream, TokenType.COMMA, TokenType.RIGHT_BRACE);

            dict.setEntry(key, value);

            stream.match(TokenType.COMMA, TokenType.RIGHT_BRACE);

            if (stream.current().getType() == TokenType.RIGHT_BRACE) {
                break;
            } else {
                stream.next();
            }
        }

        return dict;
    }
}
