package passambler.scanner;

public class ScannerException extends Exception {
    public ScannerException(String message, int line, int column) {
        super(String.format("%s on line %d, column %d", message, line, column));
    }
}