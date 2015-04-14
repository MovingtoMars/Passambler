package passambler.parser.expression.feature;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.Value;

// @TODO: seperate those two
public class IndexedOrArrayFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.LEFT_BRACKET;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenStream stream = parser.getStream();

        List<Token> tokens = new ArrayList<>();

        int brackets = 1, paren = 0, braces = 0;

        stream.next();

        ListValue inlineDeclaration = new ListValue();

        while (stream.hasNext()) {
            if ((stream.current().getType() == TokenType.COMMA || stream.current().getType() == TokenType.RIGHT_BRACKET) && brackets == 1 && paren == 0 && braces == 0) {
                if (tokens.isEmpty()) {
                    if (stream.back(2) != null) {
                        throw new ParserException(ParserExceptionType.BAD_SYNTAX, stream.current().getPosition(), "No value specified");
                    }
                } else {
                    inlineDeclaration.getValue().add(parser.createParser(new TokenStream(tokens)).parse());
                }

                if (stream.current().getType() == TokenType.RIGHT_BRACKET) {
                    brackets--;
                } else {
                    stream.next();

                    tokens.clear();

                    continue;
                }
            } else if (stream.current().getType() == TokenType.LEFT_BRACKET) {
                brackets++;
            } else if (stream.current().getType() == TokenType.RIGHT_BRACKET) {
                brackets--;
            } else if (stream.current().getType() == TokenType.LEFT_PAREN) {
                paren++;
            } else if (stream.current().getType() == TokenType.RIGHT_PAREN) {
                paren--;
            } else if (stream.current().getType() == TokenType.LEFT_BRACE) {
                braces++;
            } else if (stream.current().getType() == TokenType.RIGHT_BRACE) {
                braces--;
            }

            if (brackets == 0 && paren == 0 && braces == 0) {
                break;
            }

            tokens.add(stream.current());

            stream.next();
        }

        if (currentValue == null && (inlineDeclaration.getValue().size() > 0 || tokens.isEmpty())) {
            return inlineDeclaration;
        } else {
            Value indexValue = parser.createParser(new TokenStream(tokens)).parse();

            if (indexValue instanceof NumberValue) {
                if (!(currentValue instanceof ListValue)) {
                    throw new ParserException(ParserExceptionType.NOT_A_LIST);
                }

                ListValue list = (ListValue) currentValue;

                int index = ((NumberValue) indexValue).getValue().intValue();

                if (index < -list.getValue().size() || index > list.getValue().size() - 1) {
                    throw new ParserException(ParserExceptionType.INDEX_OUT_OF_RANGE, null, index, list.getValue().size());
                }

                if (index < 0) {
                    return list.getValue().get(list.getValue().size() - Math.abs(index));
                } else {
                    return list.getValue().get(index);
                }
            } else {
                if (!(currentValue instanceof DictValue)) {
                    throw new ParserException(ParserExceptionType.NOT_A_DICT);
                }

                DictValue dict = (DictValue) currentValue;

                if (dict.getEntry(indexValue) == null) {
                    return Value.VALUE_NIL;
                }

                return dict.getEntry(indexValue);
            }
        }
    }
}
