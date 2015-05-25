package passambler.module.net;

import java.util.Map;
import passambler.module.Module;
import passambler.module.net.function.*;
import passambler.value.Value;

public class NetModule implements Module {
    @Override
    public String getId() {
        return "net";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("listen", new ListenFunction());
        symbols.put("open", new OpenFunction());
    }
}
