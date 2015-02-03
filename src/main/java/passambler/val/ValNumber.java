package passambler.val;

import passambler.scanner.Token;

public class ValNumber extends Val {
    public ValNumber(int data) {
        this((double) data);
    }

    public ValNumber(Double data) {
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
        if (value instanceof ValNumber) {
            ValNumber newVal = new ValNumber(0);

            switch (tokenType) {
                case PLUS:
                    newVal.setValue(getValue() + ((ValNumber) value).getValue());

                    break;
                case MINUS:
                    newVal.setValue(getValue() - Math.abs(((ValNumber) value).getValue()));

                    break;
                case MULTIPLY:
                    newVal.setValue(getValue() * ((ValNumber) value).getValue());

                    break;
                case DIVIDE:
                    newVal.setValue(getValue() / ((ValNumber) value).getValue());

                    break;
                case POWER:
                    newVal.setValue(Math.pow(getValue(), ((ValNumber) value).getValue()));

                    break;
            }

            return newVal;
        } else if (value instanceof ValString && tokenType == Token.Type.PLUS) {
            return new ValString(toString() + ((ValString) value).getValue());
        } else if (value == Val.nil) {
            return this;
        }

        return null;
    }

    @Override
    public boolean isOperatorSupported(Token.Type tokenType) {
        return tokenType == Token.Type.PLUS || tokenType == Token.Type.MINUS || tokenType == Token.Type.MULTIPLY || tokenType == Token.Type.DIVIDE || tokenType == Token.Type.POWER;
    }

    @Override
    public String toString() {
        int i = (int) ((double) getValue());

        return getValue() == i ? String.valueOf(i) : String.valueOf(getValue());
    }
}
