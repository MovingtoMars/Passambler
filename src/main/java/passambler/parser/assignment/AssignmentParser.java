package passambler.parser.assignment;

import passambler.parser.expression.ExpressionParser;
import passambler.exception.ParserException;
import java.util.ArrayList;
import java.util.List;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.exception.EngineException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenType;
import passambler.parser.Parser;
import passambler.value.CharacterValue;
import passambler.value.Value;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;

public class AssignmentParser {
    private Parser parser;

    private TokenList tokens;

    public AssignmentParser(Parser parser, TokenList tokens) {
        this.parser = parser;
        this.tokens = tokens;
    }

    public void parse() throws EngineException {
        List<Token> leftTokenList = new ArrayList<>();

        while (tokens.hasNext()) {
            if (tokens.current().getType().isAssignmentOperator()) {
                break;
            }

            leftTokenList.add(tokens.current());

            tokens.next();
        }

        Token operator = tokens.current();

        tokens.next();

        Value rightValue = new ExpressionParser(parser, new TokenList(tokens.getTokensFromPosition()), true).parse();
        Value leftValue = new Value();

        TokenList leftTokens = new TokenList(leftTokenList);

        while (leftTokens.hasNext()) {
            Token token = leftTokens.current();

            if (token.getType() == TokenType.IDENTIFIER) {
                if (leftTokens.peek() == null) {
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
                leftTokens.next();

                leftTokens.match(TokenType.IDENTIFIER);

                String name = leftTokens.current().getValue();

                if (!leftValue.hasProperty(name)) {
                    throw new ParserException(ParserExceptionType.UNDEFINED_PROPERTY, tokens.current().getPosition(), name);
                }

                if (leftTokens.peek() == null) {
                    leftValue.setProperty(name, leftValue.getProperty(name).getValue().onOperator(rightValue, operator));
                } else {
                    leftValue = leftValue.getProperty(name).getValue();
                }
            } else if (token.getType() == TokenType.LEFT_BRACKET) {
                int brackets = 1;

                List<Token> bracketTokens = new ArrayList<>();

                leftTokens.next();

                while (leftTokens.hasNext()) {
                    if (leftTokens.current().getType() == TokenType.LEFT_BRACKET) {
                        brackets++;
                    } else if (leftTokens.current().getType() == TokenType.RIGHT_BRACKET) {
                        brackets--;

                        if (brackets == 0) {
                            break;
                        }
                    }

                    bracketTokens.add(leftTokens.current());

                    leftTokens.next();
                }

                leftTokens.match(TokenType.RIGHT_BRACKET);

                Value value = new ExpressionParser(parser, new TokenList(bracketTokens)).parse();

                if (value instanceof NumberValue) {
                    if (!(leftValue instanceof ListValue)) {
                        throw new ParserException(ParserExceptionType.NOT_A_LIST, tokens.current().getPosition());
                    }

                    if (leftValue instanceof StringValue && !(rightValue instanceof CharacterValue)) {
                        throw new ParserException(ParserExceptionType.NOT_A_CHARACTER, tokens.current().getPosition());
                    }

                    ListValue list = (ListValue) leftValue;

                    int index = ((NumberValue) value).getValue().intValue();

                    if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                        throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, tokens.current().getPosition(), index, list.getValue().size());
                    }

                    if (leftTokens.peek() == null) {
                        list.getValue().set(index, list.getValue().get(index).onOperator(rightValue, operator));
                    } else {
                        leftValue = list.getValue().get(index);
                    }
                } else if (leftValue instanceof DictValue) {
                    DictValue dict = (DictValue) leftValue;

                    if (leftTokens.peek() == null) {
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

            leftTokens.next();
        }
    }
}
