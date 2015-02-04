package passambler.val;

import passambler.scanner.Token;

public class ValString extends Val implements IndexAccess {
    public ValString(String data) {
        setValue(data);
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public Val onOperator(Val value, Token.Type tokenType) {
        if (value instanceof ValString && tokenType == Token.Type.PLUS) {
            return new ValString(getValue() + (value != Val.nil ? value.toString() : ""));
        }

        return null;
    }

    @Override
    public Val getIndex(int index) {
        return new ValString(String.valueOf(getValue().charAt(index)));
    }

    @Override
    public void setIndex(int index, Val value) {
        char[] array = getValue().toCharArray();

        array[index] = ((ValString) value).getValue().charAt(0);

        setValue(new String(array));
    }

    @Override
    public int getIndexCount() {
        return getValue().length();
    }
}
