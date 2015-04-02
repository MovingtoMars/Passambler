package passambler.value;

import passambler.lexer.Token;
import passambler.exception.ParserException;

public class ValueStr extends Value {
    public ValueStr(String data) {
        setValue(data);
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if (value instanceof ValueStr && (operatorToken.getType() == Token.Type.PLUS || operatorToken.getType() == Token.Type.ASSIGN_PLUS)) {
            return new ValueStr(getValue() + value.toString());
        }

        return super.onOperator(value, operatorToken);
    }
}
