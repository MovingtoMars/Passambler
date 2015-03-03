package passambler.pack.thread;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.thread.function.*;
import passambler.value.Value;

public class PackageThread implements Package {
    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Start", new FunctionStart());
        symbols.put("Sleep", new FunctionSleep());
    }
}
