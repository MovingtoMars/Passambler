package passambler.pack.net.http;

import java.util.Map;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.pack.Package;
import passambler.pack.net.http.function.*;
import passambler.value.Value;

public class PackageHttp implements Package {
    private UriHttpRequestHandlerMapper requestMapper = new UriHttpRequestHandlerMapper();

    @Override
    public String getId() {
        return "http";
    }

    @Override
    public Package[] getChildren() {
        return null;
    }
    
    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Handle", new FunctionHandle(requestMapper));
        symbols.put("Serve", new FunctionServe(requestMapper));
        symbols.put("FileHandler", new FunctionFileHandler());
    }
}
