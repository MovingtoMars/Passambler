package passambler.parser;

import passambler.parser.expression.ExpressionParser;
import passambler.exception.ParserException;
import java.util.ArrayList;
import java.util.List;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
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

    public void parse() throws EngineException {
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

        Value rightValue = new ExpressionParser(parser, new TokenStream(stream.rest()), true).parse();
        Value leftValue = new Value();

        TokenStream leftStream = new TokenStream(tokens);

        while (leftStream.hasNext()) {
            Token token = leftStream.current();

            if (token.getType() == TokenType.IDENTIFIER) {
                if (leftStream.peek() == null) {
                    if (!parser.getScope().hasSymbol(token.getValue())) {
                        parser.getScope().setSymbol(token.getValue(), rightValue);
                    } else {
                        parser.getScope().setSymbol(token.getValue(), parser.getScope().getSymbol(token.getValue()).onOperator(rightValue, operator));
                    }
                } else {
                    if (!parser.getScope().hasSymbol(token.getValue())) {
                        throw new ParserException(ParserExceptionType.UNDEFINED_VARIABLE, token.getPosition(), token.getValue());
                    }

                    leftValue = parser.getScope().getSymbol(token.getValue());
                }
            } else if (token.getType() == TokenType.PERIOD) {
                leftStream.next();

                leftStream.match(TokenType.IDENTIFIER);

                String name = leftStream.current().getValue();

                if (!leftValue.hasProperty(name)) {
                    throw new ParserException(ParserExceptionType.UNDEFINED_PROPERTY, stream.current().getPosition(), name);
                }

                if (leftStream.peek() == null) {
                    leftValue.setProperty(name, leftValue.getProperty(name).getValue().onOperator(rightValue, operator));
                } else {
                    leftValue = leftValue.getProperty(name).getValue();
                }
            } else if (token.getType() == TokenType.LBRACKET) {
                int brackets = 1;

                List<Token> bracketTokens = new ArrayList<>();

                leftStream.next();

                while (leftStream.hasNext()) {
                    if (leftStream.current().getType() == TokenType.LBRACKET) {
                        brackets++;
                    } else if (leftStream.current().getType() == TokenType.RBRACKET) {
                        brackets--;

                        if (brackets == 0) {
                            break;
                        }
                    }

                    bracketTokens.add(leftStream.current());

                    leftStream.next();
                }

                leftStream.match(TokenType.RBRACKET);

                Value value = new ExpressionParser(parser, new TokenStream(bracketTokens)).parse();

                if (value instanceof ValueNum) {
                    if (!(leftValue instanceof ValueList)) {
                        throw new ParserException(ParserExceptionType.NOT_A_LIST, stream.current().getPosition());
                    }

                    ValueList list = (ValueList) leftValue;

                    int index = ((ValueNum) value).getValue().intValue();

                    if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                        throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, stream.current().getPosition(), index, list.getValue().size());
                    }

                    if (leftStream.peek() == null) {
                        list.getValue().set(index, list.getValue().get(index).onOperator(rightValue, operator));
                    } else {
                        leftValue = list.getValue().get(index);
                    }
                } else if (leftValue instanceof ValueDict) {
                    ValueDict dict = (ValueDict) leftValue;

                    if (leftStream.peek() == null) {
                        if (dict.getEntry(value) == null) {
                            dict.setEntry(value, rightValue);
                        } else {
                            dict.setEntry(value, dict.getEntry(value).onOperator(rightValue, operator));
                        }
                    } else {
                        leftValue = dict.getEntry(value);
                    }
                }
            }

            leftStream.next();
        }
    }

    public static boolean isAssignment(TokenStream stream) {
        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.LBRACE || stream.current().getType() == TokenType.LPAREN) {
                return false;
            } else if (stream.current().getType().isAssignmentOperator()) {
                return true;
            }

            stream.next();
        }

        return false;
    }
}
