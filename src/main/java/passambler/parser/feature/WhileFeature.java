package passambler.parser.feature;

import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.BooleanValue;

public class WhileFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.get(0).getType() == TokenType.WHILE;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        tokens.next();

        tokens.match(TokenType.LEFT_PAREN);
        tokens.next();

        List<Token> expressionTokens = parser.parseExpressionTokens(tokens, TokenType.RIGHT_PAREN);

        tokens.match(TokenType.RIGHT_PAREN);

        tokens.next();

        Value value = new ExpressionParser(parser, new TokenList(expressionTokens)).parse();

        Block callback = parser.parseBlock(tokens);

        while (((BooleanValue) value).getValue()) {
            Value result = callback.invoke();

            if (result != null) {
                return result;
            }

            value = new ExpressionParser(parser, new TokenList(expressionTokens)).parse();
        }

        return null;
    }
}
