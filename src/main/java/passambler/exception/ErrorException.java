package passambler.exception;

import passambler.value.ValueError;

public class ErrorException extends EngineException {
    private ValueError error;
    
    public ErrorException(ValueError value) {
        super(value.toString());

        this.error = new ValueError(value.toString());
    }
    
    public ErrorException(Exception e) {
        this(new ValueError(e));
    }
    
    public ValueError getError() {
        return error;
    }

    @Override
    public String getName() {
        return "Uncaught error";
    }
}
