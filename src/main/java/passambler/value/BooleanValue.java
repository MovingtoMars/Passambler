package passambler.value;

import passambler.lexer.Token;
import passambler.exception.ParserException;

public class BooleanValue extends Value {
    public BooleanValue(Boolean value) {
        setValue(value);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) value;
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if (value instanceof BooleanValue) {
            switch (operatorToken.getType()) {
                case AND:
                    return new BooleanValue(getValue() == ((BooleanValue) value).getValue());
                case OR:
                    return new BooleanValue(getValue() || ((BooleanValue) value).getValue());
                case XOR:
                    return new BooleanValue(getValue() ^ ((BooleanValue) value).getValue());
                case UNARY_NOT:
                    return new BooleanValue(!getValue());
            }
        }

        return super.onOperator(value, operatorToken);
    }
}
