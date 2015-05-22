package passambler.parser.feature;

import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Argument;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.function.UserFunction;

public class FunctionFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        int offset = tokens.current().getType() == TokenType.PUB ? 1 : 0;

        return tokens.peek(offset).getType() == TokenType.FUNC && tokens.peek(offset + 1).getType() == TokenType.IDENTIFIER;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        boolean visible = tokens.current().getType() == TokenType.PUB;

        if (visible) {
            tokens.next();
        }

        tokens.next();

        tokens.match(TokenType.IDENTIFIER);

        String name = tokens.current().getValue();

        tokens.next();

        List<Argument> arguments = parser.parseArgumentDefinition(tokens);

        if (tokens.peek() != null && tokens.peek().getType() == TokenType.SEMI_COL) {
            parser.getScope().setSymbol(name, new UserFunction(null, arguments));
        } else {
            tokens.next();

            Block callback = parser.parseBlock(tokens);

            parser.getScope().setSymbol(name, new UserFunction(callback, arguments));
        }

        if (visible) {
            parser.getScope().setPublic(name);
        }

        return null;
    }
}
