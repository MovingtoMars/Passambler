package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenPosition;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.Value;

public class IndexAccessFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.LEFT_BRACKET
            && (currentValue instanceof ListValue || currentValue instanceof DictValue);
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenPosition leftBracketPosition = parser.getStream().current().getPosition();

        parser.getStream().next();

        Value indexValue = parser.getParser().parseExpression(parser.getStream(), TokenType.RIGHT_BRACKET);

        parser.getStream().match(TokenType.RIGHT_BRACKET);

        if (indexValue instanceof NumberValue) {
            if (!(currentValue instanceof ListValue)) {
                throw new ParserException(ParserExceptionType.NOT_A_LIST, leftBracketPosition);
            }

            ListValue list = (ListValue) currentValue;

            int index = ((NumberValue) indexValue).getValue().intValue();

            if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, leftBracketPosition, index, list.getValue().size());
            }

            if (index < 0) {
                return list.getValue().get(list.getValue().size() - Math.abs(index));
            } else {
                return list.getValue().get(index);
            }
        } else {
            if (!(currentValue instanceof DictValue)) {
                throw new ParserException(ParserExceptionType.NOT_A_DICT, leftBracketPosition);
            }

            DictValue dict = (DictValue) currentValue;

            if (dict.getEntry(indexValue) == null) {
                return Value.VALUE_NIL;
            }

            return dict.getEntry(indexValue);
        }
    }
}
