package passambler.exception;

public abstract class EngineException extends Exception {
    public EngineException(String message) {
        super(message);
    }
    
    public abstract String getName();
}
