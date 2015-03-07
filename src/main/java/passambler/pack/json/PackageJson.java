package passambler.pack.json;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.json.function.*;
import passambler.value.Value;

public class PackageJson implements Package {
    @Override
    public String getId() {
        return "json";
    }

    @Override
    public Package[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Encode", new FunctionEncode());
        symbols.put("Decode", new FunctionDecode());
    }
}