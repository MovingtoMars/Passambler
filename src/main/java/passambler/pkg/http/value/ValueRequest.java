package passambler.pkg.http.value;

import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
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

        setProperty("Method", new ValueStr(request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH)));
        setProperty("Uri", new ValueStr(request.getRequestLine().getUri()));

        setProperty("HostAddr", new ValueStr(((HttpInetConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION)).getRemoteAddress().getHostAddress()));
        setProperty("HostName", new ValueStr(((HttpInetConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION)).getRemoteAddress().getHostName()));
    }
}
