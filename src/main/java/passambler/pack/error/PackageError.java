package passambler.pack.error;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.error.function.*;
import passambler.value.Value;

public class PackageError implements Package {
    @Override
    public String getId() {
        return "error";
    }

    @Override
    public Package[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("New", new FunctionNew());
        symbols.put("IsError", new FunctionIsError());
    }
}