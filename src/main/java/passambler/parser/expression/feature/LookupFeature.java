package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class LookupFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.IDENTIFIER;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenList tokens = parser.getTokens();

        Token token = tokens.current();

        if (parser.getParser().getGlobals().containsKey(token.getValue())) {
            return parser.getParser().getGlobals().get(token.getValue());
        }

        if (!parser.getParser().getScope().hasSymbol(token.getValue())) {
            throw new ParserException(tokens.peek() != null && tokens.peek().getType() == TokenType.LEFT_PAREN ? ParserExceptionType.UNDEFINED_FUNCTION : ParserExceptionType.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
        }

        return parser.getParser().getScope().getSymbol(token.getValue());
    }
}
