package passambler.parser.statement;

import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Argument;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.function.UserFunction;

public class FunctionStatement implements Statement {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.current().getType() == TokenType.FUNC && tokens.peek().getType() == TokenType.IDENTIFIER;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        tokens.next();

        tokens.match(TokenType.IDENTIFIER);

        String name = tokens.current().getValue();

        tokens.next();

        List<Argument> arguments = parser.parseArgumentDefinition(tokens);

        if (tokens.peek() != null && tokens.peek().getType() == TokenType.NEW_LINE) {
            parser.getScope().setSymbol(name, new UserFunction(null, arguments));
        } else {
            tokens.next();

            Block callback = parser.parseBlock(tokens);

            parser.getScope().setSymbol(name, new UserFunction(callback, arguments));
        }

        return null;
    }
}
