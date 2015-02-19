package passambler.extension.std.value;

import java.io.InputStream;
import passambler.value.Value;

public class ValueInStream extends Value {
    private InputStream stream;

    public ValueInStream(InputStream stream) {
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }
}
