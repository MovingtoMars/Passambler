package passambler.pkg.std.value;

import java.io.PrintStream;
import passambler.value.Value;

public class ValueOutStream extends Value {
    private PrintStream stream;

    public ValueOutStream(PrintStream stream) {
        this.stream = stream;
    }

    public PrintStream getStream() {
        return stream;
    }
}
