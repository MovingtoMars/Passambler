package passambler.parser;

import passambler.value.ValueError;

public class ErrorException extends ParserException {
    private ValueError error;
    
    public ErrorException(Exception e) {
        super(e.toString());
        
        this.error = new ValueError(e);
    }
    
    public ValueError getError() {
        return error;
    }
}
