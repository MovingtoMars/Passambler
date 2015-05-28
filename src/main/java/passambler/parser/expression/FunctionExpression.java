package passambler.parser.expression;

import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.TokenType;
import passambler.parser.Argument;
import passambler.parser.Block;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;
import passambler.value.function.UserFunction;

public class FunctionExpression implements Expression {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.FUNC;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getTokens().next();

        List<Argument> arguments = parser.getParser().parseArgumentDefinition(parser.getTokens());

        parser.getTokens().next();

        Block callback = parser.getParser().parseBlock(parser.getTokens());

        return new UserFunction(callback, arguments);
    }
}
