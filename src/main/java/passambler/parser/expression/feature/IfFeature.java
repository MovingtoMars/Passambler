package passambler.parser.expression.feature;

import passambler.exception.EngineException;
import passambler.lexer.TokenType;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public class IfFeature implements Feature {
    @Override
    public boolean canPerform(ExpressionParser parser, Value currentValue) {
        return parser.getTokens().current().getType() == TokenType.IF;
    }

    @Override
    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException {
        /*
         * The if expression actually shares the same code with the if statement
         * because due to the fact that expressions can't return anything to the scope,
         * we have to keep having actual if statements.
         */
        return new passambler.parser.feature.IfFeature().perform(parser.getParser(), parser.getTokens());
    }
}
