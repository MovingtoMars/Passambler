package passambler.exception;

public enum ParserExceptionType {
    BAD_SYNTAX("bad syntax: %s"),
    INDEX_OUT_OF_RANGE("index %d out of range (size: %d)"),
    INVALID_ARGUMENT("invalid argument %d"),
    INVALID_ARGUMENT_COUNT("%d arguments expected, %d given"),
    INVALID_TOKEN("token %s expected, %s found"),
    UNDEFINED_FUNCTION("undefined function %s"),
    UNDEFINED_ARGUMENT("undefined argument %s"),
    UNDEFINED_VARIABLE("undefined variable %s"),
    UNDEFINED_PROPERTY("undefined property %s"),
    UNDEFINED_CLASS("undefined class %s"),
    UNDEFINED_DICT_ENTRY("undefined dictionary entry %s"),
    UNDEFINED_PACKAGE("undefined package %s"),
    UNEXPECTED_EOF("unexpected end of file"),
    UNEXPECTED_TOKEN("unexpected token %s"),
    UNSUPPORTED_OPERATOR("unsupported operator %s"),
    CANNOT_ITERATE("cannot iterate over this value"),
    CANNOT_USE_NAMED_ARGUMENTS("cannot use named arguments on this function"),
    ZERO_DIVISION("cannot divide by 0"),
    NOT_ALLOWED("not allowed here"),
    NOT_A_LIST("value is not a list"),
    NOT_A_DICT("value is not a dictionary"),
    NOT_A_CLASS("value is not a class"),
    EXPECTED_A_BOOL("expected a boolean");

    String message;

    ParserExceptionType() {
        this(null);
    }

    ParserExceptionType(String message) {
        this.message = message;
    }
}
