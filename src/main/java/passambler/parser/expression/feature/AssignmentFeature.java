package passambler.parser.expression.feature;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.CharacterValue;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class AssignmentFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        TokenList list = parser.getTokens().copyAtCurrentPosition();

        while (list.hasNext()) {
            if (list.current().getType() == TokenType.LEFT_BRACE || list.current().getType() == TokenType.LEFT_PAREN) {
                return false;
            } else if (list.current().getType().isAssignmentOperator()) {
                return true;
            }

            list.next();
        }

        return false;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        List<Token> leftTokenList = new ArrayList<>();

        Token pub = null;

        TokenList tokens = parser.getTokens();

        while (tokens.hasNext()) {
            if (tokens.current().getType().isAssignmentOperator()) {
                break;
            } else if (tokens.current().getType() == TokenType.PUB) {
                pub = tokens.current();
            } else {
                leftTokenList.add(tokens.current());
            }

            tokens.next();
        }

        Token operator = tokens.current();

        tokens.next();

        Value rightValue = new ExpressionParser(parser.getParser(), tokens).parse();
        Value leftValue = new Value();

        TokenList leftTokens = new TokenList(leftTokenList);

        while (leftTokens.hasNext()) {
            Token token = leftTokens.current();

            if (pub != null) {
                if (token.getType() == TokenType.IDENTIFIER && leftTokens.peek() == null) {
                    parser.getParser().getScope().setVisible(token.getValue());
                } else {
                    throw new ParserException(ParserExceptionType.INVALID_PUB_CONTEXT, pub.getPosition());
                }
            }

            if (token.getType() == TokenType.IDENTIFIER) {
                if (leftTokens.peek() == null) {
                    if (!parser.getParser().getScope().hasSymbol(token.getValue())) {
                        parser.getParser().getScope().setSymbol(token.getValue(), rightValue);
                    } else {
                        parser.getParser().getScope().setSymbol(token.getValue(), parser.getParser().getScope().getSymbol(token.getValue()).onOperator(rightValue, operator));
                    }

                    return parser.getParser().getScope().getSymbol(token.getValue());
                } else {
                    if (!parser.getParser().getScope().hasSymbol(token.getValue())) {
                        throw new ParserException(ParserExceptionType.UNDEFINED_VARIABLE, leftTokens.current().getPosition(), token.getValue());
                    }

                    leftValue = parser.getParser().getScope().getSymbol(token.getValue());
                }
            } else if (token.getType() == TokenType.PERIOD) {
                leftTokens.next();

                leftTokens.match(TokenType.IDENTIFIER);

                String name = leftTokens.current().getValue();

                if (!leftValue.hasProperty(name)) {
                    throw new ParserException(ParserExceptionType.UNDEFINED_PROPERTY, leftTokens.current().getPosition(), name);
                }

                if (leftTokens.peek() == null) {
                    leftValue.setProperty(name, leftValue.getProperty(name).getValue().onOperator(rightValue, operator));

                    return leftValue.getProperty(name).getValue();
                } else {
                    leftValue = leftValue.getProperty(name).getValue();
                }
            } else if (token.getType() == TokenType.LEFT_BRACKET) {
                leftTokens.next();

                List<Token> bracketTokens = parser.getParser().parseExpressionTokens(leftTokens, TokenType.RIGHT_BRACKET);

                leftTokens.match(TokenType.RIGHT_BRACKET);

                Value value = new ExpressionParser(parser.getParser(), new TokenList(bracketTokens)).parse();

                if (value instanceof NumberValue) {
                    if (!(leftValue instanceof ListValue)) {
                        throw new ParserException(ParserExceptionType.NOT_A_LIST, leftTokens.current().getPosition());
                    }

                    if (leftValue instanceof StringValue && !(rightValue instanceof CharacterValue)) {
                        throw new ParserException(ParserExceptionType.NOT_A_CHARACTER, leftTokens.current().getPosition());
                    }

                    ListValue list = (ListValue) leftValue;

                    int index = ((NumberValue) value).getValue().intValue();

                    if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                        throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, leftTokens.current().getPosition(), index, list.getValue().size());
                    }

                    if (leftTokens.peek() == null) {
                        list.getValue().set(index, list.getValue().get(index).onOperator(rightValue, operator));

                        return list.getValue().get(index);
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

                        return dict.getEntry(value);
                    } else {
                        leftValue = dict.getEntry(value);
                    }
                }
            }

            leftTokens.next();
        }

        return null;
    }
}
