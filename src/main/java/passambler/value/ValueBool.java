package passambler.value;

import passambler.lexer.Token;
import passambler.exception.ParserException;

public class ValueBool extends Value {
    public ValueBool(Boolean value) {
        setValue(value);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) value;
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if (value instanceof ValueBool) {
            switch (operatorToken.getType()) {
                case AND:
                    return new ValueBool(getValue() == ((ValueBool) value).getValue());
                case OR:
                    return new ValueBool(getValue() || ((ValueBool) value).getValue());
            }
        }

        return super.onOperator(value, operatorToken);
    }
}
