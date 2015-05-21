package passambler.parser.feature;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Argument;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.ClassValue;
import passambler.value.function.ClassInitializerFunction;

public class ClassFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.peek(tokens.current().getType() == TokenType.PUB ? 1 : 0).getType() == TokenType.CLASS;
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
        
        tokens.next();
        
        List<ClassInitializerFunction> parents = new ArrayList<>();
        
        if (tokens.current().getType() == TokenType.COL) {
            tokens.next();
            
            while (tokens.current().getType() != TokenType.LEFT_BRACE) {
                Value expression = parser.parseExpression(tokens, TokenType.COMMA, TokenType.RIGHT_PAREN, TokenType.LEFT_BRACE);
                
                if (!(expression instanceof ClassValue)) {
                    throw new ParserException(ParserExceptionType.NOT_A_CLASS, tokens.current().getPosition());
                }

                // Here we add the class initializer (the name of the symbol is the name of the class expression)
                parents.add((ClassInitializerFunction) parser.getScope().getSymbol(((ClassValue) expression).getName()));
            }
        }
        
        ClassInitializerFunction child = new ClassInitializerFunction(name, parser.parseBlock(tokens), arguments);
        
        for (ClassInitializerFunction parent : parents) {
            child.addParent(parent);
        }
        
        parser.getScope().setSymbol(name, child);
        
        if (visible) {
            parser.getScope().setPublic(name);
        }
        
        return null;
    }
}
