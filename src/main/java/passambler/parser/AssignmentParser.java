package passambler.parser;

import java.util.ArrayList;
import java.util.List;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;

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

                if (value instanceof ValueNum) {
                    if (!(leftValue instanceof ValueList)) {
                        throw new ParserException(ParserException.Type.NOT_A_LIST, stream.current().getPosition());
                    }

                    ValueList list = (ValueList) leftValue;

                    int index = ((ValueNum) value).getValue().intValue();

                    if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                        throw new ParserException(ParserException.Type.INDEX_OUT_OF_RANGE, stream.current().getPosition(), index, list.getValue().size());
                    }

                    if (leftStream.peek() == null) {
                        list.getValue().set(index, list.getValue().get(index).onOperator(rightValue, operator.getType()));
                    } else {
                        leftValue = list.getValue().get(index);
                    }
                } else if (leftValue instanceof ValueDict) {
                    ValueDict dict = (ValueDict) leftValue;
                    
                    if (leftStream.peek() == null) {
                        if (!dict.getValue().containsKey(value)) {
                            dict.getValue().put(value, rightValue);
                        } else {
                            dict.getValue().put(value, dict.getValue().get(value).onOperator(rightValue, operator.getType()));
                        }
                    } else {
                        leftValue = dict.getValue().get(value);
                    }
                }
            }

            leftStream.next();
        }
    }
    
    public static boolean isAssignment(TokenStream stream) {
        while (stream.hasNext()) {
            if (stream.current().getType() == Token.Type.LBRACE) {
                return false;
            } else if (stream.current().getType().isAssignmentOperator()) {
                return true;
            }
            
            stream.next();
        }
        
        return false;
    }
}
