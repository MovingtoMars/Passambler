package passambler.value;

import passambler.lexer.Token;

public class ValueNum extends Value {
    public ValueNum(int data) {
        this((double) data);
    }

    public ValueNum(Double data) {
        setValue(data);
    }

    @Override
    public Double getValue() {
        return (Double) ((double) value);
    }

    public int getValueAsInteger() {
        return getValue().intValue();
    }

    @Override
    public Value onOperator(Value value, Token.Type tokenType) {
        if (value instanceof ValueNum) {
            switch (tokenType) {
                case PLUS:
                    return new ValueNum(getValue() + ((ValueNum) value).getValue());
                case MINUS:
                    return new ValueNum(getValue() - Math.abs(((ValueNum) value).getValue()));
                case MULTIPLY:
                    return new ValueNum(getValue() * ((ValueNum) value).getValue());
                case DIVIDE:
                    return new ValueNum(getValue() / ((ValueNum) value).getValue());
                case POWER:
                    return new ValueNum(Math.pow(getValue(), ((ValueNum) value).getValue()));
                case GT:
                    return new ValueBool(getValue() > ((ValueNum) value).getValue());
                case LT:
                    return new ValueBool(getValue() < ((ValueNum) value).getValue());
                case GTE:
                    return new ValueBool(getValue() >= ((ValueNum) value).getValue());
                case LTE:
                    return new ValueBool(getValue() <= ((ValueNum) value).getValue());
            }
        }

        return super.onOperator(value, tokenType);
    }

    @Override
    public String toString() {
        int i = (int) ((double) getValue());

        return getValue() == i ? String.valueOf(i) : String.valueOf(getValue());
    }
}
