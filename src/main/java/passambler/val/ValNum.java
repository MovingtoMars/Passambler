package passambler.val;

import passambler.scanner.Token;

public class ValNum extends Val {
    public ValNum(int data) {
        this((double) data);
    }

    public ValNum(Double data) {
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
    public Val onOperator(Val value, Token.Type tokenType) {
        if (value instanceof ValNum) {
            switch (tokenType) {
                case PLUS:
                    return new ValNum(getValue() + ((ValNum) value).getValue());
                case MINUS:
                    return new ValNum(getValue() - Math.abs(((ValNum) value).getValue()));
                case MULTIPLY:
                    return new ValNum(getValue() * ((ValNum) value).getValue());
                case DIVIDE:
                    return new ValNum(getValue() / ((ValNum) value).getValue());
                case POWER:
                    return new ValNum(Math.pow(getValue(), ((ValNum) value).getValue()));
                case GT:
                    return new ValBool(getValue() > ((ValNum) value).getValue());
                case LT:
                    return new ValBool(getValue() < ((ValNum) value).getValue());
                case GTE:
                    return new ValBool(getValue() >= ((ValNum) value).getValue());
                case LTE:
                    return new ValBool(getValue() <= ((ValNum) value).getValue());
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
