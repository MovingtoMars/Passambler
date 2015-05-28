package passambler.parser.statement;

import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.Token;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.Block;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.Parser;
import passambler.util.ValueConstants;
import passambler.value.Value;
import passambler.value.BooleanValue;

public class WhileStatement implements Statement {
    @Override
    public boolean canPerform(Parser parser, TokenList tokens) {
        return tokens.current().getType() == TokenType.WHILE;
    }

    @Override
    public Value perform(Parser parser, TokenList tokens) throws EngineException {
        tokens.next();

        List<Token> expressionTokens = parser.parseExpressionTokens(tokens, TokenType.LEFT_BRACE, TokenType.ARROW);

        Value value = new ExpressionParser(parser, new TokenList(expressionTokens)).parse();

        Block callback = parser.parseBlock(tokens);

        while (expressionTokens.isEmpty() || ((BooleanValue) value).getValue()) {
            Value result = callback.invoke();

            if (result != null) {
                if (result == ValueConstants.SKIP) {
                    continue;
                } else if (result == ValueConstants.STOP) {
                    break;
                } else {
                    return result;
                }
            }

            if (!expressionTokens.isEmpty()) {
                value = new ExpressionParser(parser, new TokenList(expressionTokens)).parse();
            }
        }

        return null;
    }
}
