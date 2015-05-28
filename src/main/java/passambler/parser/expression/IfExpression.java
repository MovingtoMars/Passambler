package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.parser.statement.IfStatement;
import passambler.value.Value;

public class IfExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.IF;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        /*
         * The if expression actually shares the same code with the if statement
         * because due to the fact that expressions can't return anything to the scope,
         * we have to keep having an actual if statement.
         */
        return new IfStatement().perform(parser.getParser(), parser.getTokens());
    }
}
