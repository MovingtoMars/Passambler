package passambler.pack.net;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.net.function.*;
import passambler.pack.net.http.HttpPackage;
import passambler.value.Value;

public class NetPackage implements Package {
    @Override
    public String getId() {
        return "net";
    }

    @Override
    public Package[] getChildren() {
        return new Package[] { new HttpPackage() };
    }
    
    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Listen", new ListenFunction());
        symbols.put("Open", new OpenFunction());
        symbols.put("Accept", new AcceptFunction());
        symbols.put("Close", new CloseFunction());
    }
}
