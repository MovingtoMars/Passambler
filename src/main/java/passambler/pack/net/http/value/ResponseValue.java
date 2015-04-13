package passambler.pack.net.http.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import passambler.value.Value;
import passambler.value.DictValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.WriteableValue;

public class ResponseValue extends Value implements WriteableValue {
    private StringBuilder responseData = new StringBuilder();

    public ResponseValue(HttpContext context, HttpResponse response) {
        setProperty("Headers", new DictValue());
        setProperty("Status", new NumberValue(200));
    }

    public int getStatus() {
        return ((NumberValue) getProperty("Status").getValue()).getValue().intValue();
    }

    public Header[] getHeaders() {
        if (getProperty("Headers").getValue() instanceof DictValue) {
            List<Header> headers = new ArrayList<>();

            for (Map.Entry<Value, Value> entry : ((DictValue) getProperty("Headers").getValue()).getValue().entrySet()) {
                if (entry.getKey() instanceof StringValue && entry.getValue() instanceof StringValue) {
                    headers.add(new BasicHeader(((StringValue) entry.getKey()).getValue(), ((StringValue) entry.getValue()).getValue()));
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
