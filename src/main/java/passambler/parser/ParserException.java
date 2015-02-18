package passambler.parser;

import passambler.lexer.SourcePosition;

public class ParserException extends Exception {
    public enum Type {
        BAD_SYNTAX("bad syntax: %s"),
        INDEX_OUT_OF_RANGE("index %d out of range (size: %d)"),
        NOT_INDEXED("value not indexed"),
        INVALID_ARGUMENT("invalid argument %d"),
        INVALID_ARGUMENT_COUNT("%d arguments expected, %d given"),
        INVALID_TOKEN("token %s expected, %s found"),
        UNDEFINED_PROCEDURE("undefined procedure %s"),
        UNDEFINED_VARIABLE("undefined variable %s"),
        UNDEFINED_PROPERTY("undefined property %s"),
        UNEXPECTED_EOF("unexpected end of file"),
        UNEXPECTED_TOKEN("unexpected token %s"),
        UNSUPPORTED_OPERATOR("unsupported operator %s"),
        ZERO_DIVISION("cannot divide by 0"),
        NOT_ALLOWED("not allowed here");

        String message;

        Type() {
            this(null);
        }

        Type(String message) {
            this.message = message;
        }
    }

    public ParserException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ParserException(Type type, Object... args) {
        this(type.message, args);
    }

    public ParserException(Type type, SourcePosition position, Object... args) {
        this(type.message + " at line " + position.getLine() + ", column " + position.getColumn(), args);
    }
}
