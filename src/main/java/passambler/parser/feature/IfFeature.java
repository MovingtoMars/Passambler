package passambler.parser.feature;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenStream;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.ExpressionParser;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.ValueBool;

public class IfFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenStream stream) {
        return stream.first().getType() == TokenType.IF;
    }

    @Override
    public Value perform(Parser parser, TokenStream stream) throws EngineException {
        stream.next();

        boolean elseCondition = false;

        Map<ValueBool, Block> cases = new LinkedHashMap();

        while (stream.hasNext()) {
            if (!elseCondition) {
                stream.match(TokenType.LPAREN);
                stream.next();

                List<Token> tokens = parser.parseExpressionTokens(stream, TokenType.RPAREN);

                stream.match(TokenType.RPAREN);

                Value condition = new ExpressionParser(parser, new TokenStream(tokens)).parse();

                if (!(condition instanceof ValueBool)) {
                    throw new ParserException(ParserExceptionType.EXPECTED_A_BOOL, tokens.get(0).getPosition());
                }

                stream.next();

                cases.put((ValueBool) condition, parser.parseBlock(stream));

                tokens.clear();
            } else {
                cases.put(new ValueBool(true), parser.parseBlock(stream));
            }

            stream.next();

            if (stream.current() != null) {
                if (elseCondition) {
                    throw new ParserException(ParserExceptionType.BAD_SYNTAX, stream.first().getPosition(), "else should be the last statement");
                }

                stream.match(TokenType.ELSE, TokenType.ELSEIF);

                if (stream.current().getType() == TokenType.ELSE) {
                    elseCondition = true;
                }

                stream.next();
            }
        }

        for (Map.Entry<ValueBool, Block> entry : cases.entrySet()) {
            if (entry.getKey().getValue() == true) {
                Value result = entry.getValue().invoke();

                if (result != null) {
                    return result;
                }

                break;
            }
        }

        return null;
    }
}