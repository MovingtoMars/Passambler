package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.util.ValueConstants;
import passambler.value.Value;

public class LeaveFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.get(0).getType() == TokenType.RETURN || tokens.get(0).getType() == TokenType.BREAK || tokens.get(0).getType() == TokenType.CONTINUE;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        TokenType type = tokens.current().getType();

        tokens.next();

        if (type == TokenType.RETURN) {
            Value result = new ExpressionParser(parser, tokens.copyAtCurrentPosition()).parse();

            return result == null ? ValueConstants.NIL : result;
        } else if (type == TokenType.BREAK) {
            return ValueConstants.BREAK;
        } else if (type == TokenType.CONTINUE) {
            return ValueConstants.CONTINUE;
        }

        return null;
    }
}
