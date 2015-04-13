package passambler.lexer;

public class Token {
    private TokenType type;

    private TokenPosition position;

    private String value;

    public Token(TokenType type, TokenPosition position) {
        this(type, null, position);
    }

    public Token(TokenType type, String value, TokenPosition position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public TokenPosition getPosition() {
        return position;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Integer getValueAsInteger() {
        return Integer.valueOf(value);
    }

    public void setType(TokenType type) {
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
