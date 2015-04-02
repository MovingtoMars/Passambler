package passambler.exception;

import passambler.lexer.SourcePosition;

public class ParserException extends EngineException {
    public ParserException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ParserException(ParserExceptionType type, Object... args) {
        this(type.message, args);
    }

    public ParserException(ParserExceptionType type, SourcePosition position, Object... args) {
        this(type.message + (position != null ? " at line " + position.getLine() + ", column " + position.getColumn() : ""), args);
    }

    @Override
    public String getName() {
        return "Parser error";
    }
}
