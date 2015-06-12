package passambler.value;

import passambler.exception.ParserException;
import passambler.lexer.Token;
import passambler.lexer.TokenType;

public class CharacterValue extends NumberValue {
    public CharacterValue(int character) {
        super(character);
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if ((value instanceof StringValue || value instanceof CharacterValue) && (operatorToken.getType() == TokenType.PLUS || operatorToken.getType() == TokenType.ASSIGN_PLUS)) {
            return new StringValue(toString() + value.toString());
        }

        Value newValue = super.onOperator(value, operatorToken);

        if (newValue instanceof NumberValue) {
            return new CharacterValue(((NumberValue) newValue).getValue().intValue());
        }

        return newValue;
    }

    @Override
    public String toString() {
        return String.valueOf(Character.toChars(getValue().intValue()));
    }
}
