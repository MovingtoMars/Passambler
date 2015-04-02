package passambler.lexer;

public enum TokenType {
    IDENTIFIER, STRING, NUMBER,
    LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACE, RBRACE,
    ASSIGN, ASSIGN_PLUS, ASSIGN_MINUS, ASSIGN_MULTIPLY, ASSIGN_DIVIDE, ASSIGN_POWER, ASSIGN_MODULO,
    PLUS, MINUS, MULTIPLY, DIVIDE, POWER, MODULO, COMPARE, RANGE,
    EQUAL, NEQUAL, GT, LT, GTE, LTE, NOT,
    AND, OR,
    WHILE, FOR, FN, RETURN, IF, ELSEIF, ELSE,
    CLASS,
    PERIOD, COMMA, SEMI_COL, COL,
    TRY, CATCH;

    public boolean isOperator() {
        return this == PLUS || this == MINUS || this == MULTIPLY || this == DIVIDE || this == POWER || this == MODULO
            || this == GT || this == LT || this == GTE || this == LTE
            || this == EQUAL || this == NEQUAL
            || this == AND || this == OR
            || this == COMPARE || this == RANGE;
    }

    public boolean isAssignmentOperator() {
        return this == ASSIGN
            || this == ASSIGN_PLUS || this == ASSIGN_MINUS || this == ASSIGN_MULTIPLY || this == ASSIGN_DIVIDE || this == ASSIGN_POWER || this == ASSIGN_MODULO;
    }

    public boolean isLineInsensitive() {
        return this == ELSE || this == ELSEIF
            || this == CATCH
            || isOperator();
    }
}