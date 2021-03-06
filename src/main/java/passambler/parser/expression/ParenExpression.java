package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.lexer.TokenList;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class ParenExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.LEFT_PAREN;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getTokens().next();

        return new ExpressionParser(parser.getParser(), new TokenList(parser.getParser().parseExpressionTokens(parser.getTokens(), TokenType.RIGHT_PAREN))).parse();
    }
}
