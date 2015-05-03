package passambler.bundle.net.http.value;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import passambler.util.ValueConstants;
import passambler.value.Property;
import passambler.value.Value;
import passambler.value.DictValue;
import passambler.value.StringValue;

public class RequestValue extends Value {
    public RequestValue(HttpContext context, HttpRequest request) {
        setProperty("Headers", new Property() {
            @Override
            public Value getValue() {
                DictValue headers = new DictValue();

                for (Header header : request.getAllHeaders()) {
                    headers.getValue().put(new StringValue(header.getName()), new StringValue(header.getValue()));
                }

                return headers;
            }
        });

        DictValue form = new DictValue();
        DictValue query = new DictValue();

        try {
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;

                URLEncodedUtils.parse(enclosingRequest.getEntity()).stream().forEach((pair) -> {
                    form.setEntry(new StringValue(pair.getName()), pair.getValue().trim().isEmpty() ? ValueConstants.NIL : new StringValue(pair.getValue()));
                });
            }

            URLEncodedUtils.parse(new URI(request.getRequestLine().getUri()), "UTF-8").stream().forEach((pair) -> {
                query.setEntry(new StringValue(pair.getName()), new StringValue(pair.getValue()));
            });
        } catch (IOException | URISyntaxException e) {

        }

        setProperty("Form", form);
        setProperty("Query", query);

        setProperty("Method", new StringValue(request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH)));
        setProperty("Uri", new StringValue(request.getRequestLine().getUri()));

        setProperty("HostAddr", new StringValue(((HttpInetConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getRemoteAddress().getHostAddress()));
        setProperty("HostName", new StringValue(((HttpInetConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getRemoteAddress().getHostName()));
    }
}
