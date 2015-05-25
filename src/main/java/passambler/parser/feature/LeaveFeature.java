package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Parser;
import passambler.util.ValueConstants;
import passambler.value.Value;

public class LeaveFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.current().getType() == TokenType.RETURN || tokens.current().getType() == TokenType.STOP || tokens.current().getType() == TokenType.SKIP;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        TokenType type = tokens.current().getType();

        tokens.next();

        if (type == TokenType.RETURN) {
            Value result = parser.parseExpression(tokens);

            return result == null ? ValueConstants.NIL : result;
        } else if (type == TokenType.STOP) {
            return ValueConstants.STOP;
        } else if (type == TokenType.SKIP) {
            return ValueConstants.SKIP;
        }

        return null;
    }
}
