package passambler.parser.feature;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.BooleanValue;

public class IfFeature implements Feature {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.get(0).getType() == TokenType.IF;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        tokens.next();

        boolean elseCondition = false;

        Map<BooleanValue, Block> cases = new LinkedHashMap();

        while (tokens.hasNext()) {
            if (!elseCondition) {
                tokens.match(TokenType.LEFT_PAREN);
                tokens.next();

                List<Token> expressionTokens = parser.parseExpressionTokens(tokens, TokenType.RIGHT_PAREN);

                tokens.match(TokenType.RIGHT_PAREN);

                Value condition = new ExpressionParser(parser, new TokenList(expressionTokens)).parse();

                if (!(condition instanceof BooleanValue)) {
                    throw new ParserException(ParserExceptionType.NOT_A_BOOLEAN, expressionTokens.get(0).getPosition());
                }

                tokens.next();

                cases.put((BooleanValue) condition, parser.parseBlock(tokens));

                expressionTokens.clear();
            } else {
                cases.put(new BooleanValue(true), parser.parseBlock(tokens));
            }

            tokens.next();

            if (tokens.current() != null) {
                if (elseCondition) {
                    throw new ParserException(ParserExceptionType.BAD_SYNTAX, tokens.get(0).getPosition(), "Else block should be the last statement");
                }

                tokens.match(TokenType.ELSE, TokenType.ELSEIF);

                if (tokens.current().getType() == TokenType.ELSE) {
                    elseCondition = true;
                }

                tokens.next();
            }
        }

        for (Map.Entry<BooleanValue, Block> entry : cases.entrySet()) {
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
