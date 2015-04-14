package passambler.parser.expression.feature;

import java.util.ArrayList;
import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.DictValue;
import passambler.value.Value;

// @TODO: seperate these two
public class DictOrBlockFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.LEFT_BRACE;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        TokenStream stream = parser.getStream();

        int braces = 1, paren = 0, brackets = 0;

        stream.next();

        DictValue value = new DictValue();

        List<Token> tokens = new ArrayList<>();

        while (stream.hasNext()) {
            if (stream.current().getType() == TokenType.LEFT_BRACKET) {
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

            if ((stream.current().getType() == TokenType.COMMA || stream.current().getType() == TokenType.RIGHT_BRACE) && braces <= 1 && paren == 0 && brackets == 0) {
                // @TODO: figure out why I did this
                if (tokens.isEmpty()) {
                    break;
                }

                TokenStream element = new TokenStream(tokens);

                List<Token> valueTokens = new ArrayList<>();

                while (element.hasNext()) {
                    if (element.current().getType() == TokenType.COL) {
                        break;
                    }

                    valueTokens.add(element.current());

                    element.next();
                }

                element.match(TokenType.COL);
                element.next();

                value.setEntry(parser.createParser(new TokenStream(valueTokens)).parse(), parser.createParser(new TokenStream(element.rest())).parse());

                tokens.clear();

                if (stream.current().getType() == TokenType.COMMA) {
                    stream.next();

                    continue;
                } else {
                    break;
                }
            }

            tokens.add(stream.current());

            stream.next();
        }

        return value;
    }
}