package passambler.parser.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.util.ValueConstants;
import passambler.value.Value;

public class ReturnFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.get(0).getType() == TokenType.RETURN;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        tokens.next();

        Value result = new ExpressionParser(parser, tokens.copyAtCurrentPosition()).parse();

        return result == null ? ValueConstants.NIL : result;
    }
}
