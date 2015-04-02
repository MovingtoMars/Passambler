package passambler.parser.expression;

import passambler.lexer.Token;
import passambler.value.Value;

public class ValueOperatorPair {
    private Value value;

    private Token operator;

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
}
