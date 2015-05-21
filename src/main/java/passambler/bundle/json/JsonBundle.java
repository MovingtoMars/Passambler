package passambler.bundle.json;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.json.function.*;
import passambler.value.Value;

public class JsonBundle implements Bundle {
    @Override
    public String getId() {
        return "json";
    }

    @Override
    public Bundle[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("encode", new EncodeFunction());
        symbols.put("decode", new DecodeFunction());
    }
}
