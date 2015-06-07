package passambler.lexer;

public enum TokenType {
    IDENTIFIER, STRING, NUMBER, HEX,
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE,
    PLUS, MINUS, MULTIPLY, DIVIDE, POWER, MODULO, COMPARE, RANGE, INCLUSIVE_RANGE, TERNARY,
    ASSIGN, ASSIGN_PLUS, ASSIGN_MINUS, ASSIGN_MULTIPLY, ASSIGN_DIVIDE, ASSIGN_POWER, ASSIGN_MODULO,
    UNARY_PLUS, UNARY_MINUS, UNARY_NOT,
    EQUAL, NEQUAL, GT, LT, GTE, LTE,
    AND, OR, XOR,
    ARROW,
    RETURN, STOP, SKIP,
    WHILE, FOR, FUNC, IF, ELSEIF, ELSE, DEFER, TRY, CATCH, FINALLY, PUB, IN, IMPORT, MATCH, DEFAULT,
    PERIOD, COMMA, NEW_LINE, EXCLUSIVE_SLICE, INCLUSIVE_SLICE, AS;

    public boolean isOperator() {
        return this == PLUS || this == MINUS || this == MULTIPLY || this == DIVIDE || this == POWER || this == MODULO
                || this == GT || this == LT || this == GTE || this == LTE
                || this == EQUAL || this == NEQUAL
                || this == AND || this == OR || this == XOR
                || this == COMPARE || this == RANGE || this == INCLUSIVE_RANGE;
    }

    public boolean isUnaryOperator() {
        return this == UNARY_PLUS || this == UNARY_MINUS
                || this == UNARY_NOT;
    }

    public boolean isAssignmentOperator() {
        return this == ASSIGN
                || this == ASSIGN_PLUS || this == ASSIGN_MINUS || this == ASSIGN_MULTIPLY || this == ASSIGN_DIVIDE || this == ASSIGN_POWER || this == ASSIGN_MODULO;
    }

    public boolean isAfterRightBrace() {
        return this == ELSE || this == ELSEIF
                || this == CATCH || this == FINALLY;
    }

    public boolean isTreatedAsBlockOpeningInExpression() {
        return this == ARROW
                || this == IF
                || isAssignmentOperator();
    }
}
