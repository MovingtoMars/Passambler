package passambler.bundle.net;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.net.function.*;
import passambler.value.Value;

public class NetBundle implements Bundle {
    @Override
    public String getId() {
        return "net";
    }

    @Override
    public Bundle[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Listen", new ListenFunction());
        symbols.put("Open", new OpenFunction());
    }
}
