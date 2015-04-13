package passambler.value;

import passambler.lexer.Token;
import passambler.exception.ParserException;
import passambler.lexer.TokenType;

public class StringValue extends Value {
    public StringValue(String data) {
        setValue(data);
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if (value instanceof StringValue && (operatorToken.getType() == TokenType.PLUS || operatorToken.getType() == TokenType.ASSIGN_PLUS)) {
            return new StringValue(getValue() + value.toString());
        }

        return super.onOperator(value, operatorToken);
    }
}
