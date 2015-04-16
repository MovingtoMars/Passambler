package passambler.exception;

import passambler.value.ErrorValue;

public class ErrorException extends EngineException {
    private ErrorValue error;

    public ErrorException(ErrorValue value) {
        super(value.toString());

        this.error = new ErrorValue(value.toString());
    }

    public ErrorException(Exception e) {
        this(new ErrorValue(e));
    }

    public ErrorValue getError() {
        return error;
    }

    @Override
    public String getName() {
        return "Uncaught error";
    }
}
