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
        return parser.getTokens().current().getType() == TokenType.LEFT_BRACKET
            && (currentValue instanceof ListValue || currentValue instanceof DictValue);
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getTokens().next();

        TokenPosition indexPosition = parser.getTokens().current().getPosition();

        Value indexValue = parser.getParser().parseExpression(parser.getTokens(), TokenType.RIGHT_BRACKET);

        parser.getTokens().match(TokenType.RIGHT_BRACKET);

        if (indexValue instanceof NumberValue && currentValue instanceof ListValue) {
            ListValue list = (ListValue) currentValue;

            int index = ((NumberValue) indexValue).getValue().intValue();

            if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, indexPosition, index, list.getValue().size());
            }

            if (index < 0) {
                return list.getValue().get(list.getValue().size() - Math.abs(index));
            } else {
                return list.getValue().get(index);
            }
        } else {
            DictValue dict = (DictValue) currentValue;

            if (dict.getEntry(indexValue) == null) {
                throw new ParserException(ParserExceptionType.UNDEFINED_DICT_ENTRY, indexPosition, indexValue.toString());
            }

            return dict.getEntry(indexValue);
        }
    }
}
