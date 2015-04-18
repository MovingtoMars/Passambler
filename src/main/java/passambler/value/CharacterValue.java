package passambler.value;

import passambler.exception.ParserException;
import passambler.lexer.Token;
import passambler.lexer.TokenType;

public class CharacterValue extends Value {
    public CharacterValue(int character) {
        setValue(character);
    }

    @Override
    public Integer getValue() {
        return (Integer) value;
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if ((value instanceof StringValue || value instanceof CharacterValue) && (operatorToken.getType() == TokenType.PLUS || operatorToken.getType() == TokenType.ASSIGN_PLUS)) {
            return new StringValue(toString() + value.toString());
        }

        return super.onOperator(value, operatorToken);
    }

    @Override
    public String toString() {
        return String.valueOf(Character.toChars(getValue()));
    }
}
