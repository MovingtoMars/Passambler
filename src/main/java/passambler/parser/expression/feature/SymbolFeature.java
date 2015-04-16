package passambler.parser.expression.feature;

import java.math.BigDecimal;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class SymbolFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.IDENTIFIER
            || parser.getStream().current().getType() == TokenType.NUMBER
            || parser.getStream().current().getType() == TokenType.STRING;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenStream stream = parser.getStream();

        Token token = stream.current();

        if (token.getType() == TokenType.STRING) {
            return new StringValue(token.getValue());
        } else if (token.getType() == TokenType.NUMBER) {
            StringBuilder number = new StringBuilder();

            number.append(token.getValue());

            if (stream.peek() != null && stream.peek().getType() == TokenType.PERIOD) {
                stream.next();
                stream.next();

                stream.match(TokenType.NUMBER);

                number.append(".");
                number.append(stream.current().getValue());
            }

            return new NumberValue(new BigDecimal(number.toString()));
        } else if (token.getType() == TokenType.IDENTIFIER) {
            if (token.getValue().equals("true")) {
                return Value.VALUE_TRUE;
            } else if (token.getValue().equals("false")) {
                return Value.VALUE_FALSE;
            } else if (token.getValue().equals("nil")) {
                return Value.VALUE_NIL;
            }

            if (!parser.getParser().getScope().hasSymbol(token.getValue())) {
                throw new ParserException(stream.peek() != null && stream.peek().getType() == TokenType.LEFT_PAREN ? ParserExceptionType.UNDEFINED_FUNCTION : ParserExceptionType.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
            }

            return parser.getParser().getScope().getSymbol(token.getValue());
        }

        return null;
    }
}
