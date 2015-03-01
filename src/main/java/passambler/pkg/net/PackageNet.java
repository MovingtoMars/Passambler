package passambler.pkg.net;

import java.util.Map;
import passambler.pkg.Package;
import passambler.pkg.net.function.*;
import passambler.value.Value;

public class PackageNet implements Package {
    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Listen", new FunctionListen());
        symbols.put("Accept", new FunctionAccept());
        symbols.put("Close", new FunctionClose());
    }
}
