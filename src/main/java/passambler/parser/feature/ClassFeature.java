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
import passambler.value.ClassValue;
import passambler.value.function.ClassInitializerFunction;

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

        List<ArgumentDefinition> arguments = parser.parseArgumentDefinition(stream);

        stream.next();

        List<ClassInitializerFunction> parents = new ArrayList<>();

        if (stream.current().getType() == TokenType.COL) {
            stream.next();

            while (stream.current().getType() != TokenType.LEFT_BRACE) {
                Value expression = parser.parseExpression(stream, TokenType.COMMA, TokenType.RIGHT_PAREN, TokenType.LEFT_BRACE);

                if (!(expression instanceof ClassValue)) {
                    throw new ParserException(ParserExceptionType.NOT_A_CLASS, stream.current().getPosition());
                }

                // Here we add the class initializer (the name of the symbol is the name of the class expression)
                parents.add((ClassInitializerFunction) parser.getScope().getSymbol(((ClassValue) expression).getName()));
            }
        }

        ClassInitializerFunction child = new ClassInitializerFunction(name, parser.parseBlock(stream), arguments);

        for (ClassInitializerFunction parent : parents) {
            child.addParent(parent);
        }

        parser.getScope().setSymbol(name, child);

        return null;
    }
}
