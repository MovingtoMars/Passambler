package passambler.parser.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.util.ValueConstants;
import passambler.value.Value;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;

public class ForFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.current().getType() == TokenType.FOR;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        List<String> arguments = new ArrayList<>();

        tokens.next();

        TokenList left = new TokenList(parser.parseExpressionTokens(tokens, TokenType.LEFT_BRACE, TokenType.ARROW, TokenType.COL));
        Value right = null;

        if (tokens.current().getType() == TokenType.COL) {
            tokens.next();

            right = parser.parseExpression(tokens, TokenType.LEFT_BRACE, TokenType.ARROW);
        }

        Block callback = parser.parseBlock(tokens);

        if (right != null) {
            while (left.hasNext()) {
                left.match(TokenType.IDENTIFIER);

                arguments.add(left.current().getValue());

                if (left.peek() != null) {
                    left.next();

                    left.match(TokenType.COMMA);
                }

                left.next();
            }
        }

        Value value = right == null ? new ExpressionParser(parser, left.copy()).parse() : right;

        if (value instanceof ListValue) {
            ListValue list = (ListValue) value;

            for (int i = 0; i < list.getValue().size(); ++i) {
                if (arguments.size() == 1) {
                    callback.getParser().getScope().setSymbol(arguments.get(0), list.getValue().get(i));
                } else if (arguments.size() == 2) {
                    callback.getParser().getScope().setSymbol(arguments.get(0), new NumberValue(i));
                    callback.getParser().getScope().setSymbol(arguments.get(1), list.getValue().get(i));
                } else if (arguments.size() > 2) {
                    throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, tokens.get(0).getPosition(), 2, arguments.size());
                }

                Value result = callback.invoke();

                if (result != null) {
                    if (result == ValueConstants.BREAK) {
                        break;
                    } else if (result != ValueConstants.CONTINUE) {
                        return result;
                    }
                }
            }
        } else if (value instanceof DictValue) {
            DictValue dict = (DictValue) value;

            for (Map.Entry<Value, Value> entry : dict.getValue().entrySet()) {
                if (arguments.size() == 1) {
                    callback.getParser().getScope().setSymbol(arguments.get(0), entry.getValue());
                } else if (arguments.size() == 2) {
                    callback.getParser().getScope().setSymbol(arguments.get(0), entry.getKey());
                    callback.getParser().getScope().setSymbol(arguments.get(1), entry.getValue());
                } else if (arguments.size() > 2) {
                    throw new ParserException(ParserExceptionType.INVALID_ARGUMENT_COUNT, tokens.get(0).getPosition(), 2, arguments.size());
                }

                Value result = callback.invoke();

                if (result != null) {
                    if (result == ValueConstants.BREAK) {
                        break;
                    } else if (result != ValueConstants.CONTINUE) {
                        return result;
                    }
                }
            }
        } else {
            throw new ParserException(ParserExceptionType.CANNOT_ITERATE, tokens.get(0).getPosition());
        }

        return null;
    }
}
