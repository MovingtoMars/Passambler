package passambler.parser.feature;

import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.ValueBool;

public class WhileFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return stream.first().getType() == TokenType.WHILE;
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        stream.next();

        stream.match(TokenType.LPAREN);
        stream.next();

        List<Token> tokens = parser.parseExpressionTokens(stream, TokenType.RPAREN);

        stream.match(TokenType.RPAREN);

        stream.next();

        Value value = new ExpressionParser(parser, new TokenStream(tokens)).parse();

        Block callback = parser.parseBlock(stream);

        while (((ValueBool) value).getValue()) {
            Value result = callback.invoke();

            if (result != null) {
                return result;
            }

            value = new ExpressionParser(parser, new TokenStream(tokens)).parse();
        }
        return null;
    }
}
