package passambler.lexer;

public class TokenPosition {
    private int line;
    private int column;

    public TokenPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
