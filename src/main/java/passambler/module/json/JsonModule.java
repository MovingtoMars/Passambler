package passambler.module.json;

import java.util.Map;
import passambler.module.Module;
import passambler.module.json.function.*;
import passambler.value.Value;

public class JsonModule implements Module {
    @Override
    public String getId() {
        return "json";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("encode", new EncodeFunction());
        symbols.put("decode", new DecodeFunction());
    }
}
