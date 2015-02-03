package passambler.val;

import passambler.scanner.Token;

public abstract class Val {
    public static ValNil nil = new ValNil();

    protected Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Val onOperator(Val value, Token.Type tokenType) {
        return null;
    }

    public boolean isOperatorSupported(Token.Type tokenType) {
        return false;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
