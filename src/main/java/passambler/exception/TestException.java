package passambler.exception;

public class TestException extends EngineException {
    public TestException(String message, Object... args) {
        super(String.format(message, args));
    }

    @Override
    public String getName() {
        return "Test error";
    }
}
