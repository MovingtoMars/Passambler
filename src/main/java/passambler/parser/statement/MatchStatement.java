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

        tokens.match(TokenType.LEFT_BRACE);

        tokens.next();
        tokens.next();

        Map<Value, Block> cases = new LinkedHashMap();

        while (tokens.hasNext()) {
            Value expression = null;

            if (tokens.current().getType() == TokenType.DEFAULT) {
                expression = value;

                tokens.next();
            } else if (tokens.current().getType() == TokenType.RIGHT_BRACE) {
                tokens.next();

                break;
            } else {
                expression = parser.parseExpression(tokens, TokenType.EXCLUSIVE_SLICE);
            }

            tokens.match(TokenType.EXCLUSIVE_SLICE);
            tokens.next();

            Block block = parser.parseBlock(tokens);

            cases.put(expression, block);

            tokens.next();

            if (tokens.current().getType() == TokenType.NEW_LINE) {
                tokens.next();
                tokens.match(TokenType.RIGHT_BRACE);
                tokens.next();
                break;
            } else {
                tokens.match(TokenType.COMMA);
                tokens.next();
            }

            tokens.next();
        }

        for (Map.Entry<Value, Block> entry : cases.entrySet()) {
            if (entry.getKey().equals(value)) {
                return entry.getValue().invoke();
            }
        }

        return null;
    }
}
