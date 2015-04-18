package passambler.parser.expression;

import passambler.lexer.Token;
import passambler.value.Value;

public class ValueOperatorPair {
    private Value value;

    private Token operator;

    public ValueOperatorPair(Value value, Token operator) {
        this.value = value;
        this.operator = operator;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Token getOperator() {
        return operator;
    }

    public void setOperator(Token operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (operator != null) {
            builder.append(operator).append(" ");
        }

        builder.append(value);

        return builder.toString();
    }
}
