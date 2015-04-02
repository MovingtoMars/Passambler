package passambler.parser.feature;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.ValueClass;
import passambler.value.function.FunctionClassInitializer;

public class ClassFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return stream.first().getType() == TokenType.CLASS;
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        stream.next();
        stream.match(TokenType.IDENTIFIER);

        String name = stream.current().getValue();

        stream.next();

        List<ArgumentDefinition> arguments = parser.argumentDefinitions(stream);

        stream.next();

        List<FunctionClassInitializer> parents = new ArrayList<>();

        if (stream.current().getType() == TokenType.COL) {
            stream.next();

            while (stream.current().getType() != TokenType.LBRACE) {
                Value expression = parser.expression(stream, TokenType.COMMA, TokenType.RPAREN, TokenType.LBRACE);

                if (!(expression instanceof ValueClass)) {
                    throw new ParserException(ParserExceptionType.NOT_A_CLASS, stream.current().getPosition());
                }

                // Here we add the class initializer (the name of the symbol is the name of the class expression)
                parents.add((FunctionClassInitializer) parser.getScope().getSymbol(((ValueClass) expression).getName()));
            }
        }

        FunctionClassInitializer child = new FunctionClassInitializer(name, parser.block(stream), arguments);

        for (FunctionClassInitializer parent : parents) {
            child.addParent(parent);
        }

        parser.getScope().setSymbol(name, child);

        return null;
    }
}
