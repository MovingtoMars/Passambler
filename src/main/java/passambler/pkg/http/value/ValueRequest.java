package passambler.pkg.http.value;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.client.utils.URLEncodedUtils;
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
        ValueDict query = new ValueDict();

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;

            try {
                URLEncodedUtils.parse(enclosingRequest.getEntity()).stream().forEach((pair) -> {
                    form.setEntry(new ValueStr(pair.getName()), new ValueStr(pair.getName()));
                });
            } catch (IOException e) {
            }
        }

        URLEncodedUtils.parse(request.getRequestLine().getUri(), Charset.forName("UTF-8")).stream().forEach((pair) -> {
            query.setEntry(new ValueStr(pair.getName()), new ValueStr(pair.getName()));
        });

        setProperty("Form", form);
        setProperty("Query", query);

        setProperty("Method", new ValueStr(request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH)));
        setProperty("Uri", new ValueStr(request.getRequestLine().getUri()));

        setProperty("HostAddr", new ValueStr(((HttpInetConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getRemoteAddress().getHostAddress()));
        setProperty("HostName", new ValueStr(((HttpInetConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getRemoteAddress().getHostName()));
    }
}
