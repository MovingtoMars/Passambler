package passambler.pack.net;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.net.function.*;
import passambler.pack.net.http.PackageHttp;
import passambler.value.Value;

public class PackageNet implements Package {
    @Override
    public String getId() {
        return "net";
    }

    @Override
    public Package[] getChildren() {
        return new Package[] { new PackageHttp() };
    }
    
    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Listen", new FunctionListen());
        symbols.put("Open", new FunctionOpen());
        symbols.put("Accept", new FunctionAccept());
        symbols.put("Close", new FunctionClose());
    }
}
