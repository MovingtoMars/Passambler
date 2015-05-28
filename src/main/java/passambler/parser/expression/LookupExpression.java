package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class LookupExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.IDENTIFIER;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenList tokens = parser.getTokens();

        String name = tokens.current().getValue();

        if (parser.getParser().getGlobals().containsKey(name)) {
            return parser.getParser().getGlobals().get(name);
        }

        if (!parser.getParser().getScope().hasSymbol(name)) {
            throw new ParserException(tokens.peek() != null && tokens.peek().getType() == TokenType.LEFT_PAREN ? ParserExceptionType.UNDEFINED_FUNCTION : ParserExceptionType.UNDEFINED_VARIABLE, tokens.current().getPosition(), name);
        }

        return parser.getParser().getScope().getSymbol(name);
    }
}
