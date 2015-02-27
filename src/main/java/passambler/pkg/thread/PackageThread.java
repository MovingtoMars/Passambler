package passambler.pkg.thread;

import java.util.Map;
import passambler.pkg.Package;
import passambler.pkg.thread.function.*;
import passambler.value.Value;

public class PackageThread implements Package {
    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Start", new FunctionStart());
    }
}