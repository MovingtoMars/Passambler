package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenPosition;
import passambler.lexer.TokenType;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.Value;

public class IndexAccessExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.LEFT_BRACKET && (currentValue instanceof ListValue || currentValue instanceof DictValue);
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getTokens().next();

        TokenPosition leftPosition = parser.getTokens().current().getPosition();

        Value left = parser.getParser().parseExpression(parser.getTokens(), TokenType.RIGHT_BRACKET, TokenType.INCLUSIVE_SLICE, TokenType.EXCLUSIVE_SLICE);
        Value right = null;

        boolean inclusiveRight = false;
        boolean noRight = false;

        // We can only use slice syntax on lists
        if ((parser.getTokens().current().getType() == TokenType.EXCLUSIVE_SLICE || parser.getTokens().current().getType() == TokenType.INCLUSIVE_SLICE) && currentValue instanceof ListValue) {
            Token rightToken = parser.getTokens().current();

            parser.getTokens().next();

            if (parser.getTokens().current().getType() == TokenType.RIGHT_BRACKET) {
                noRight = true;
            } else {
                right = parser.getParser().parseExpression(parser.getTokens(), TokenType.RIGHT_BRACKET);

                if (!(right instanceof NumberValue)) {
                    throw new ParserException(ParserExceptionType.NOT_A_NUMBER, rightToken.getPosition());
                }

                inclusiveRight = rightToken.getType() == TokenType.INCLUSIVE_SLICE;
            }
        }

        parser.getTokens().match(TokenType.RIGHT_BRACKET);

        if (left instanceof NumberValue && currentValue instanceof ListValue) {
            ListValue list = (ListValue) currentValue;

            ListValue data = new ListValue();

            int leftIndex = ((NumberValue) left).getValue().intValue();
            int rightIndex = right != null ? ((NumberValue) right).getValue().intValue() : leftIndex + 1;

            for (int i = leftIndex; i < (noRight ? list.getValue().size() : rightIndex + (inclusiveRight ? 1 : 0)); ++i) {
                if (i < -list.getValue().size() || i > list.getValue().size() - 1) {
                    throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, leftPosition, i, list.getValue().size());
                }

                if (i < 0) {
                    data.getValue().add(list.getValue().get(list.getValue().size() - Math.abs(i)));
                } else {
                    data.getValue().add(list.getValue().get(i));
                }
            }

            return data.getValue().size() == 1 ? data.getValue().get(0) : data;
        } else {
            DictValue dict = (DictValue) currentValue;

            if (dict.getEntry(left) == null) {
                throw new ParserException(ParserExceptionType.UNDEFINED_DICT_ENTRY, leftPosition, left.toString());
            }

            return dict.getEntry(left);
        }
    }
}
