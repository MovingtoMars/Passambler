package passambler.lexer;

public class Token {
    public enum Type {
        IDENTIFIER, STRING, NUMBER,
        LPAREN, RPAREN,
        LBRACKET, RBRACKET,
        COMMA,
        WHILE, FOR,
        PERIOD, RANGE,
        ASSIGN, ASSIGN_PLUS, ASSIGN_MINUS, ASSIGN_MULTIPLY, ASSIGN_DIVIDE, ASSIGN_POWER, ASSIGN_MODULO,
        PLUS, MINUS, MULTIPLY, DIVIDE, POWER, MODULO, COMPARE,
        SEMI_COL, COL,
        LBRACE, RBRACE,
        EQUAL, NEQUAL, GT, LT, GTE, LTE,
        AND, OR,
        PROC, RETURN, IMPORT,
        IF, ELSEIF, ELSE;

        public boolean isOperator() {
            return this == PLUS || this == MINUS || this == MULTIPLY || this == DIVIDE || this == POWER || this == MODULO || this == GT || this == LT || this == GTE || this == LTE || this == EQUAL || this == NEQUAL || this == AND || this == OR || this == COMPARE || this == RANGE;
        }

        public boolean isAssignmentOperator() {
            return this == ASSIGN || this == ASSIGN_PLUS || this == ASSIGN_MINUS || this == ASSIGN_MULTIPLY || this == ASSIGN_DIVIDE || this == ASSIGN_POWER || this == ASSIGN_MODULO;
        }
    }

    private Type type;

    private SourcePosition position;

    private String value;

    public Token(Type type, SourcePosition position) {
        this(type, null, position);
    }

    public Token(Type type, String value, SourcePosition position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public SourcePosition getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Integer getValueAsInteger() {
        return Integer.valueOf(value);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return type + (value != null ? " (\"" + value + "\")" : "");
    }
}
