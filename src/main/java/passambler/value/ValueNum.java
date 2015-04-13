package passambler.value;

import java.math.BigDecimal;
import passambler.lexer.Token;
import passambler.exception.ParserException;
import passambler.exception.ParserExceptionType;

public class ValueNum extends Value {
    public ValueNum(double data) {
        this(new BigDecimal(data));
    }

    public ValueNum(int data) {
        this(new BigDecimal(data));
    }

    public ValueNum(BigDecimal data) {
        setValue(data);
    }

    @Override
    public BigDecimal getValue() {
        return (BigDecimal) value;
    }

    @Override
    public Value onOperator(Value value, Token operatorToken) throws ParserException {
        if (value instanceof ValueNum) {
            switch (operatorToken.getType()) {
                case PLUS:
                case ASSIGN_PLUS:
                    return new ValueNum(getValue().add(((ValueNum) value).getValue()));
                case MINUS:
                case ASSIGN_MINUS:
                    return new ValueNum(getValue().subtract(((ValueNum) value).getValue()));
                case MULTIPLY:
                case ASSIGN_MULTIPLY:
                    return new ValueNum(getValue().multiply(((ValueNum) value).getValue()));
                case DIVIDE:
                case ASSIGN_DIVIDE:
                    if (((ValueNum) value).getValue().intValue() == 0) {
                        throw new ParserException(ParserExceptionType.CANNOT_DIVIDE_BY_ZERO, operatorToken.getPosition());
                    }
                    
                    return new ValueNum(getValue().divide(((ValueNum) value).getValue()));
                case POWER:
                case ASSIGN_POWER:
                    return new ValueNum(getValue().pow(((ValueNum) value).getValue().intValue()));
                case MODULO:
                case ASSIGN_MODULO:
                    return new ValueNum(getValue().remainder(((ValueNum) value).getValue()));
                case GT:
                    return new ValueBool(getValue().compareTo(((ValueNum) value).getValue()) > 0);
                case LT:
                    return new ValueBool(getValue().compareTo(((ValueNum) value).getValue()) < 0);
                case GTE:
                    return new ValueBool(getValue().compareTo(((ValueNum) value).getValue()) >= 0);
                case LTE:
                    return new ValueBool(getValue().compareTo(((ValueNum) value).getValue()) <= 0);
                case RANGE:
                    ValueList list = new ValueList();

                    int min = getValue().intValue();
                    int max = ((ValueNum) value).getValue().intValue();

                    if (max > min) {
                        for (int i = min; i <= max; ++i) {
                            list.getValue().add(new ValueNum(i));
                        }
                    } else {
                        for (int i = min; i >= max; --i) {
                            list.getValue().add(new ValueNum(i));
                        }
                    }

                    return list;
                case COMPARE:
                    return new ValueNum(getValue().compareTo(((ValueNum) value).getValue()));
            }
        }

        return super.onOperator(value, operatorToken);
    }

    @Override
    public String toString() {
        return getValue().toPlainString();
    }
}
