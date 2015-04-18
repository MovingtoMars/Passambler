package passambler.value;

import java.math.BigDecimal;
import passambler.lexer.Token;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;

public class NumberValue extends Value {
    public NumberValue(double data) {
        this(new BigDecimal(data));
    }

    public NumberValue(int data) {
        this(new BigDecimal(data));
    }

    public NumberValue(BigDecimal data) {
        setValue(data);
    }

    @Override
    public BigDecimal getValue() {
        return (BigDecimal) value;
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if (value instanceof NumberValue) {
            switch (operatorToken.getType()) {
                case PLUS:
                case ASSIGN_PLUS:
                    return new NumberValue(getValue().add(((NumberValue) value).getValue()));
                case MINUS:
                case ASSIGN_MINUS:
                    return new NumberValue(getValue().subtract(((NumberValue) value).getValue()));
                case MULTIPLY:
                case ASSIGN_MULTIPLY:
                    return new NumberValue(getValue().multiply(((NumberValue) value).getValue()));
                case DIVIDE:
                case ASSIGN_DIVIDE:
                    if (((NumberValue) value).getValue().intValue() == 0) {
                        throw new ParserException(ParserExceptionType.CANNOT_DIVIDE_BY_ZERO, operatorToken.getPosition());
                    }

                    return new NumberValue(getValue().divide(((NumberValue) value).getValue()));
                case POWER:
                case ASSIGN_POWER:
                    return new NumberValue(getValue().pow(((NumberValue) value).getValue().intValue()));
                case MODULO:
                case ASSIGN_MODULO:
                    return new NumberValue(getValue().remainder(((NumberValue) value).getValue()));
                case GT:
                    return new BooleanValue(getValue().compareTo(((NumberValue) value).getValue()) > 0);
                case LT:
                    return new BooleanValue(getValue().compareTo(((NumberValue) value).getValue()) < 0);
                case GTE:
                    return new BooleanValue(getValue().compareTo(((NumberValue) value).getValue()) >= 0);
                case LTE:
                    return new BooleanValue(getValue().compareTo(((NumberValue) value).getValue()) <= 0);
                case RANGE:
                    ListValue list = new ListValue();

                    int min = getValue().intValue();
                    int max = ((NumberValue) value).getValue().intValue();

                    if (max > min) {
                        for (int i = min; i <= max; ++i) {
                            list.getValue().add(new NumberValue(i));
                        }
                    } else {
                        for (int i = min; i >= max; --i) {
                            list.getValue().add(new NumberValue(i));
                        }
                    }

                    return list;
                case COMPARE:
                    return new NumberValue(getValue().compareTo(((NumberValue) value).getValue()));
                case UNARY_PLUS:
                    return new NumberValue(getValue().plus());
                case UNARY_MINUS:
                    return new NumberValue(getValue().negate());
                case UNARY_INCR:
                    return new NumberValue(getValue().add(new BigDecimal(1)));
                case UNARY_DECR:
                    return new NumberValue(getValue().min(new BigDecimal(1)));

            }
        }

        return super.onOperator(value, operatorToken);
    }

    @Override
    public String toString() {
        return getValue().toPlainString();
    }
}
