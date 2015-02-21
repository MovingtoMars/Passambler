package passambler.value;

import passambler.lexer.Token;

public class ValueStr extends Value implements IndexedValue {
    public ValueStr(String data) {
        setValue(data);
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public Value onOperator(Value value, Token.Type tokenType) {
        if (value instanceof ValueStr && (tokenType == Token.Type.PLUS || tokenType == Token.Type.ASSIGN_PLUS)) {
            return new ValueStr(getValue() + value.toString());
        }

        return super.onOperator(value, tokenType);
    }

    @Override
    public Value getIndex(Value key) {
        return new ValueStr(String.valueOf(getValue().charAt(((ValueNum) key).getValue().intValue())));
    }

    @Override
    public void setIndex(Value key, Value value) {
        char[] array = getValue().toCharArray();

        array[((ValueNum) key).getValue().intValue()] = ((ValueStr) value).getValue().charAt(0);

        setValue(new String(array));
    }

    @Override
    public int getIndexCount() {
        return getValue().length();
    }
}
