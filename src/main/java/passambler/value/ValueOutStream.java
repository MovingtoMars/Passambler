package passambler.value;

import java.io.PrintStream;

public class ValueOutStream extends Value {
    private PrintStream stream;

    public ValueOutStream(PrintStream stream) {
        this.stream = stream;
    }

    public PrintStream getStream() {
        return stream;
    }
}
