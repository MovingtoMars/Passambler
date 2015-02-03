package passambler.scanner;

public class SourcePosition {
    private int line;
    private int column;

    public SourcePosition(int line, int column) {
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
