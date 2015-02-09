package passambler.lexer;

public class Token {
    public enum Type {
        IDENTIFIER, STRING, NUMBER,
        LPAREN, RPAREN,
        LBRACKET, RBRACKET,
        COMMA,
        WHILE, FOR, IN,
        DOT, DOT_DOUBLE,
        ASSIGN, ASSIGN_LOCKED,
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

    private Object value;

    public Token(Type type, SourcePosition position) {
        this(type, null, position);
    }

    public Token(Type type, Object value, SourcePosition position) {
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

    public Object getValue() {
        return value;
    }

    public Integer getIntValue() {
        return Integer.valueOf((String) value);
    }

    public String getStringValue() {
        return String.valueOf(value);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return type + " (" + value + ")";
    }
}
