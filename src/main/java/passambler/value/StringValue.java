package passambler.value;

import passambler.lexer.Token;
import passambler.exception.ParserException;
import passambler.lexer.TokenType;

public class StringValue extends ListValue {
    private int cachedList;
    private String cached;

    public StringValue(String data) {
        for (char character : data.toCharArray()) {
            list.add(new CharacterValue(character));
        }

        cached = data;
        cachedList = list.hashCode();
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
        if (list.hashCode() != cachedList) {
            StringBuilder builder = new StringBuilder();

            getValue().stream().forEach((item) -> builder.append(item.toString()));

            cached = builder.toString();
            cachedList = list.hashCode();
        }

        return cached;
    }
}
