package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.value.IndexedValue;
import passambler.value.Value;

public class AssignmentParser {
    private Parser parser;

    private TokenStream stream;

    public AssignmentParser(Parser parser, TokenStream stream) {
        this.parser = parser;

        this.stream = stream;
    }

    public void parse() throws ParserException {
        List<Token> tokens = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType().isAssignmentOperator()) {
                break;
            }

            tokens.add(stream.current());

            stream.next();
        }

        Token operator = stream.current();

        stream.next();
        
        Value rightValue = new ExpressionParser(parser, new TokenStream(stream.rest())).parse();
        Value leftValue = new Value();

        TokenStream leftStream = new TokenStream(tokens);

        while (leftStream.hasNext()) {
            Token token = leftStream.current();

            if (token.getType() == Token.Type.IDENTIFIER) {
                if (leftStream.peek() == null) {
                    if (!parser.getScope().hasSymbol(token.getValue())) {
                        parser.getScope().setSymbol(token.getValue(), rightValue);
                    } else {
                        parser.getScope().setSymbol(token.getValue(), parser.getScope().getSymbol(token.getValue()).onOperator(rightValue, operator.getType()));
                    }
                } else {
                    if (!parser.getScope().hasSymbol(token.getValue())) {
                        throw new ParserException(ParserException.Type.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
                    }
                    
                    leftValue = parser.getScope().getSymbol(token.getValue());
                }
            } else if (token.getType() == Token.Type.PERIOD) {
                leftStream.next();

                leftStream.match(Token.Type.IDENTIFIER);

                String name = leftStream.current().getValue();
                
                if (!leftValue.hasProperty(name)) {
                    throw new ParserException(ParserException.Type.UNDEFINED_PROPERTY, stream.current().getPosition(), name);
                }

                if (leftStream.peek() == null) {
                    leftValue.setProperty(name, leftValue.getProperty(name).getValue().onOperator(rightValue, operator.getType()));
                } else {
                    leftValue = leftValue.getProperty(name).getValue();
                }
            } else if (token.getType() == Token.Type.LBRACKET) {
                int brackets = 1;

                List<Token> bracketTokens = new ArrayList<>();
                
                leftStream.next();
                
                while (leftStream.hasNext()) {
                    if (leftStream.current().getType() == Token.Type.LBRACKET) {
                        brackets++;
                    } else if (leftStream.current().getType() == Token.Type.RBRACKET) {
                        brackets--;
                        
                        if (brackets == 0) {
                            break;
                        }
                    }

                    bracketTokens.add(leftStream.current());

                    leftStream.next();
                }
                
                leftStream.match(Token.Type.RBRACKET);
                
                Value value = new ExpressionParser(parser, new TokenStream(bracketTokens)).parse();
                
                IndexedValue indexedValue = (IndexedValue) leftValue;
                
                if (leftStream.peek() == null) {
                    if (indexedValue.getIndex(value) == null) {
                        indexedValue.setIndex(value, rightValue);
                    } else {
                        indexedValue.setIndex(value, indexedValue.getIndex(value).onOperator(rightValue, operator.getType()));
                    }
                } else {
                    leftValue = indexedValue.getIndex(value);
                }
            }

            leftStream.next();
        }
    }
}
