package passambler.lexer;

public enum TokenType {
    IDENTIFIER, STRING, NUMBER,
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE,
    PLUS, MINUS, MULTIPLY, DIVIDE, POWER, MODULO, COMPARE, RANGE, TERNARY,
    ASSIGN, ASSIGN_PLUS, ASSIGN_MINUS, ASSIGN_MULTIPLY, ASSIGN_DIVIDE, ASSIGN_POWER, ASSIGN_MODULO,
    UNARY_PLUS, UNARY_MINUS, UNARY_NOT,
    EQUAL, NEQUAL, GT, LT, GTE, LTE,
    AND, OR, XOR,
    ARROW,
    WHILE, FOR, FUNC, RETURN, IF, ELSEIF, ELSE, CLASS, TRY, CATCH, FINALLY,
    PERIOD, COMMA, SEMI_COL, COL;

    public boolean isOperator() {
        return this == PLUS || this == MINUS || this == MULTIPLY || this == DIVIDE || this == POWER || this == MODULO
                || this == GT || this == LT || this == GTE || this == LTE
                || this == EQUAL || this == NEQUAL
                || this == AND || this == OR || this == XOR
                || this == COMPARE || this == RANGE;
    }

    public boolean isUnaryOperator() {
        return this == UNARY_PLUS || this == UNARY_MINUS
                || this == UNARY_NOT;
    }

    public boolean isAssignmentOperator() {
        return this == ASSIGN
                || this == ASSIGN_PLUS || this == ASSIGN_MINUS || this == ASSIGN_MULTIPLY || this == ASSIGN_DIVIDE || this == ASSIGN_POWER || this == ASSIGN_MODULO;
    }

    public boolean isLineInsensitive() {
        return this == ELSE || this == ELSEIF
                || this == CATCH || this == FINALLY
                || this == ARROW
                || isOperator();
    }
}
