package passambler.pkg.http.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import passambler.value.Property;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueStr;

public class ValueRequest extends Value {
    public ValueRequest(HttpContext context, HttpRequest request) {
        setProperty("Headers", new Property() {
            @Override
            public Value getValue() {
                ValueDict headers = new ValueDict();

                for (Header header : request.getAllHeaders()) {
                    headers.getValue().put(new ValueStr(header.getName()), new ValueStr(header.getValue()));
                }

                return headers;
            }
        });

        ValueDict form = new ValueDict();

        try {
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;

                for (String element : new BufferedReader(new InputStreamReader(enclosingRequest.getEntity().getContent())).readLine().split("&")) {
                    String[] keyAndValue = element.split("=");

                    if (keyAndValue.length > 0) {
                        Value key = new ValueStr(URLDecoder.decode(keyAndValue[0], "UTF-8"));
                        Value value = keyAndValue.length > 1 ? new ValueStr(URLDecoder.decode(keyAndValue[1], "UTF-8")) : Value.VALUE_NIL;

                        form.getValue().put(key, value);
                    }
                }
            }
        } catch (IOException | IllegalStateException e) {
        }

        setProperty("Form", form);

        ValueDict query = new ValueDict();

        try {
            String[] queryParts = request.getRequestLine().getUri().split("\\?");

            if (queryParts.length > 1) {
                String queryData = queryParts[1];

                for (String element : queryData.split("&")) {
                    String[] keyAndValue = element.split("=");

                    if (keyAndValue.length > 0) {
                        Value key = new ValueStr(URLDecoder.decode(keyAndValue[0], "UTF-8"));
                        Value value = keyAndValue.length > 1 ? new ValueStr(URLDecoder.decode(keyAndValue[1], "UTF-8")) : Value.VALUE_NIL;

                        query.getValue().put(key, value);
                    }
                }
            }
        } catch (Exception e) {
        }
        
        setProperty("Query", query);

        setProperty("Method", new ValueStr(request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH)));
        setProperty("Uri", new ValueStr(request.getRequestLine().getUri()));

        setProperty("HostAddr", new ValueStr(((HttpInetConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getRemoteAddress().getHostAddress()));
        setProperty("HostName", new ValueStr(((HttpInetConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getRemoteAddress().getHostName()));
    }
}
