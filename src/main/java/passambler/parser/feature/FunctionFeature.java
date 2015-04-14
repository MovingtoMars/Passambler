package passambler.parser.feature;

import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.function.UserFunction;

public class FunctionFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return stream.first().getType() == TokenType.FN;
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        stream.next();

        stream.match(TokenType.IDENTIFIER);

        String name = stream.current().getValue();

        stream.next();

        List<ArgumentDefinition> arguments = parser.parseArgumentDefinition(stream);

        if (stream.peek() == null) {
            parser.getScope().setSymbol(name, new UserFunction(null, arguments));
        } else {
            stream.next();

            String typehintName = null;

            if (stream.current().getType() == TokenType.COL) {
                stream.next();

                stream.match(TokenType.IDENTIFIER);
                
                typehintName = stream.current().getValue();

                stream.next();
            }

            Block callback = parser.parseBlock(stream);

            UserFunction function = new UserFunction(callback, arguments);

            if (typehintName != null) {
                if (!parser.getTypehints().containsKey(typehintName)) {
                    throw new ParserException(ParserExceptionType.UNDEFINED_TYPEHINT, stream.current().getPosition(), typehintName);
                }

                function.setTypehint(parser.getTypehints().get(typehintName));
            }

            parser.getScope().setSymbol(name, function);
        }

        return null;
    }
}
