package passambler.pkg.http.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueNum;
import passambler.value.ValueStr;
import passambler.value.WriteHandler;

public class ValueResponse extends Value implements WriteHandler {
    private StringBuilder responseData = new StringBuilder();

    public ValueResponse(HttpContext context, HttpResponse response) {
        setProperty("Headers", new ValueDict());

        setProperty("Status", new ValueNum(200));
    }

    public int getStatus() {
        return ((ValueNum) getProperty("Status").getValue()).getValue().intValue();
    }

    public Header[] getHeaders() {
        if (getProperty("Headers").getValue() instanceof ValueDict) {
            List<Header> headers = new ArrayList<>();

            for (Map.Entry<Value, Value> entry : ((ValueDict) getProperty("Headers").getValue()).getValue().entrySet()) {
                if (entry.getKey() instanceof ValueStr && entry.getValue() instanceof ValueStr) {
                    headers.add(new BasicHeader(((ValueStr) entry.getKey()).getValue(), ((ValueStr) entry.getValue()).getValue()));
                }
            }

            return headers.toArray(new Header[headers.size()]);
        }

        return new Header[]{};
    }

    @Override
    public void write(Value value) {
        responseData.append(value.toString());
    }

    public String getResponseData() {
        return responseData.toString();
    }
}
