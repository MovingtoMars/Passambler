package passambler.tests;

public class TestException extends Exception {
    public TestException(String message, Object... args) {
        super(String.format(message, args));
    }
}
