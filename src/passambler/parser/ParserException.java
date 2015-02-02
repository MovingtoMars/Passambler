package passambler.parser;

import passambler.scanner.SourcePosition;

public class ParserException extends Exception {
    public enum Type {
        BAD_SYNTAX("bad syntax: %s"),
        INDEX_OUT_OF_RANGE("index %d out of range (size: %d)"),
        NOT_INDEXED("cannot access index on a variable that is not indexed"),
        INVALID_ARGUMENT("invalid argument %d on function %s"),
        INVALID_ARGUMENT_COUNT("function %s expects %d arguments, %d given"),
        UNDEFINED_FUNCTION("undefined function %s"),
        UNDEFINED_VARIABLE("undefined variable %s"),
        UNEXPECTED_EOF("unexpected end of file"),
        UNEXPECTED_TOKEN("unexpected token"),
        UNSUPPORTED_OPERATOR("unsupported operator %s"),
        ZERO_DIVISION("cannot divide by 0");

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