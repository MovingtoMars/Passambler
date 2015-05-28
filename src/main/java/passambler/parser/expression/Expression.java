package passambler.parser.expression;

import passambler.exception.EngineException;
import passambler.parser.expression.ExpressionParser;
import passambler.value.Value;

public interface Expression {
    public boolean canPerform(ExpressionParser parser, Value currentValue);

    public Value perform(ExpressionParser parser, Value currentValue) throws EngineException;
}
