package passambler.parser.expression.feature;

import java.util.List;
import passambler.exception.EngineException;
import passambler.lexer.TokenType;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Block;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;
import passambler.value.function.UserFunction;

public class AnonymousFunctionFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getStream().current().getType() == TokenType.FN;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        parser.getStream().next();

        List<ArgumentDefinition> arguments = parser.getParser().parseArgumentDefinition(parser.getStream());

        parser.getStream().next();

        Block callback = parser.getParser().parseBlock(parser.getStream());

        return new UserFunction(callback, arguments);
    }
}
