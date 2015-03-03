package passambler.pack.net;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.net.function.*;
import passambler.value.Value;

public class PackageNet implements Package {
    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Listen", new FunctionListen());
        symbols.put("Accept", new FunctionAccept());
        symbols.put("Close", new FunctionClose());
    }
}
