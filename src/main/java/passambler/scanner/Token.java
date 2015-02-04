package passambler.scanner;

public class Token {
    public enum Type {
        IDENTIFIER, STRING, NUMBER,
        LPAREN, RPAREN,
        LBRACKET, RBRACKET,
        COMMA,
        DOT,
        ASSIGN, ASSIGN_LOCKED,
        PLUS, MINUS, MULTIPLY, DIVIDE, POWER,
        SEMICOL, PIPE,
        LBRACE, RBRACE, STREAM, STREAM_REVERSE,
        EQUAL, NEQUAL, GT, LT, GTE, LTE,
        AND, OR
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
        return type + "(" + value + ")";
    }
}
