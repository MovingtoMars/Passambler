package passambler.value;

import java.io.InputStream;

public class ValueInStream extends Value {
    private InputStream stream;

    public ValueInStream(InputStream stream) {
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }
}
