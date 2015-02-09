package passambler.value;

import passambler.lexer.Token;

public class ValueBool extends Value {
    public ValueBool(Boolean value) {
        setValue(value);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) value;
    }
    
    @Override
    public Value onOperator(Value value, Token.Type tokenType) {
        if (value instanceof ValueBool) {
            switch (tokenType) {
                case AND:
                    return new ValueBool(getValue() == ((ValueBool) value).getValue());
                case OR:
                    return new ValueBool(getValue() || ((ValueBool) value).getValue());
            }
        }
        
        return super.onOperator(value, tokenType);
    }
}
