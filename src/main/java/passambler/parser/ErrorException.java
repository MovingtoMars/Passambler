package passambler.parser;

import passambler.value.ValueError;

public class ErrorException extends ParserException {
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
}
