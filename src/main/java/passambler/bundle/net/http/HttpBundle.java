package passambler.bundle.net.http;

import java.util.Map;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import passambler.bundle.Bundle;
import passambler.bundle.net.http.function.*;
import passambler.value.Value;

public class HttpBundle implements Bundle {
    private UriHttpRequestHandlerMapper requestMapper = new UriHttpRequestHandlerMapper();

    @Override
    public String getId() {
        return "http";
    }

    @Override
    public Bundle[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Handle", new HandleFunction(requestMapper));
        symbols.put("Serve", new ServeFunction(requestMapper));
        symbols.put("FileHandler", new FileHandlerFunction());
    }
}
