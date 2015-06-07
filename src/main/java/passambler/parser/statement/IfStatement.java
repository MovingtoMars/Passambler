package passambler.parser.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import passambler.exception.EngineException;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.Parser;
import passambler.value.Value;
import passambler.value.BooleanValue;

public class IfStatement implements Statement {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.current().getType() == TokenType.IF;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        tokens.next();

        boolean elseCondition = false;

        Map<BooleanValue, Block> cases = new LinkedHashMap();

        while (tokens.hasNext()) {
            if (!elseCondition) {
                cases.put((BooleanValue) parser.parseBooleanExpression(tokens, TokenType.LEFT_BRACE, TokenType.ARROW), parser.parseBlock(tokens));
            } else {
                cases.put(new BooleanValue(true), parser.parseBlock(tokens));
            }

            tokens.next();

            if (tokens.current() != null) {
                if (elseCondition) {
                    throw new ParserException(ParserExceptionType.UNEXPECTED_TOKEN, tokens.current().getPosition(), tokens.current().getType());
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
                return entry.getValue().invoke();
            }
        }

        return null;
    }
}
