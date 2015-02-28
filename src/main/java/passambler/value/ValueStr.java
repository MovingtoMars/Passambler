package passambler.value;

import passambler.lexer.Token;

public class ValueStr extends Value {
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
}
