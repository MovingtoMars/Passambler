package passambler.pack.http;

import java.util.Map;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.pack.Package;
import passambler.pack.http.function.*;
import passambler.value.Value;

public class PackageHttp implements Package {
    public static UriHttpRequestHandlerMapper REQUEST_HANDLER = new UriHttpRequestHandlerMapper();

    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Handle", new FunctionHandle());
        symbols.put("Serve", new FunctionServe());
        symbols.put("FileHandler", new FunctionFileHandler());
    }
}
