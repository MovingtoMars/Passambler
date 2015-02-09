package passambler.lexer;

public class LexerException extends Exception {
    public LexerException(String message, int line, int column) {
        super(String.format("%s on line %d, column %d", message, line, column));
    }
}
