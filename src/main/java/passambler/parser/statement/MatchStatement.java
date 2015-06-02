package passambler.parser.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.Value;

public class MatchStatement implements Statement {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.current().getType() == TokenType.MATCH;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        tokens.next();

        Value value = parser.parseExpression(tokens, TokenType.LEFT_BRACE);

        TokenList block = new TokenList(parser.parseBlock(tokens).getTokens());

        Map<Value, Block> cases = new LinkedHashMap();

        while (block.getTokens().stream().filter(t -> t.getType() != TokenType.NEW_LINE).count() > 0 && block.hasNext()) {
            Value expression = null;

            if (block.current().getType() == TokenType.DEFAULT) {
                expression = value;

                block.next();
            } else {
                expression = parser.parseExpression(block, TokenType.LEFT_BRACE, TokenType.ARROW);
            }

            cases.put(expression, parser.parseBlock(block));

            block.next();

            if (block.current() != null) {
                block.match(TokenType.COMMA, TokenType.NEW_LINE);

                if (block.current().getType() == TokenType.COMMA) {
                    block.next();
                }

                block.next();
            }
        }

        for (Map.Entry<Value, Block> entry : cases.entrySet()) {
            if (entry.getKey().equals(value)) {
                return entry.getValue().invoke();
            }
        }

        return null;
    }
}
