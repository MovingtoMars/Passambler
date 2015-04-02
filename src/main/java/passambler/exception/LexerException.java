package passambler.exception;

public class LexerException extends EngineException {
    public LexerException(String message, int line, int column) {
        super(String.format("%s on line %d, column %d", message, line, column));
    }

    @Override
    public String getName() {
        return "Lexer error";
    }
}
