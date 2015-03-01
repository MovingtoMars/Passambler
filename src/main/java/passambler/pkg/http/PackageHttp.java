package passambler.pkg.http;

import java.util.Map;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.pkg.Package;
import passambler.pkg.http.function.*;
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
