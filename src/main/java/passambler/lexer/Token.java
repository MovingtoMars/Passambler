package passambler.lexer;

public class Token {
    public enum Type {
        IDENTIFIER, STRING, NUMBER,
        LPAREN, RPAREN,
        LBRACKET, RBRACKET,
        COMMA,
        WHILE, FOR, IN,
        DOT, DOT_DOUBLE,
        ASSIGN, EXCL,
        PLUS, MINUS, MULTIPLY, DIVIDE, POWER,
        COL, NEW_LINE,
        LBRACE, RBRACE,
        EQUAL, NEQUAL, GT, LT, GTE, LTE,
        AND, OR,
        RETURN;
        
        public boolean isOperator() {
            return this == PLUS || this == MINUS || this == MULTIPLY || this == DIVIDE || this == POWER || this == GT || this == LT || this == GTE || this == LTE || this == EQUAL || this == NEQUAL || this == AND || this == OR;
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
        return type + " (" + value + ")";
    }
}
