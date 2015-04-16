package passambler.exception;

public enum ParserExceptionType {
    BAD_SYNTAX("Bad syntax: %s"),
    INDEX_OUT_OF_RANGE("Index %d out of range (size: %d)"),
    INVALID_ARGUMENT("Invalid argument %d"),
    INVALID_ARGUMENT_COUNT("%d arguments expected, %d given"),
    INVALID_TOKEN("Token %s expected, %s found"),
    UNDEFINED_FUNCTION("Undefined function '%s'"),
    UNDEFINED_ARGUMENT("Undefined argument '%s'"),
    UNDEFINED_VARIABLE("Undefined variable '%s'"),
    UNDEFINED_PROPERTY("Undefined property '%s'"),
    UNDEFINED_CLASS("Undefined class '%s'"),
    UNDEFINED_DICT_ENTRY("Undefined dictionary entry '%s'"),
    UNEXPECTED_EOF("Unexpected end of file"),
    UNEXPECTED_TOKEN("Unexpected token '%s'"),
    UNSUPPORTED_OPERATOR("Unsupported operator '%s'"),
    CANNOT_ITERATE("Cannot iterate over this value"),
    CANNOT_USE_NAMED_ARGUMENTS("Cannot use named arguments on this function"),
    CANNOT_DIVIDE_BY_ZERO("Cannot divide by 0"),
    NOT_ALLOWED("Not allowed here"),
    NOT_A_LIST("Value is not a list"),
    NOT_A_DICT("Value is not a dictionary"),
    NOT_A_CLASS("Value is not a class"),
    EXPECTED_A_BOOL("Expected a boolean");

    String message;

    ParserExceptionType() {
        this(null);
    }

    ParserExceptionType(String message) {
        this.message = message;
    }
}
