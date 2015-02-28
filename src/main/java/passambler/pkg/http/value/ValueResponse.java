package passambler.pkg.http.value;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import passambler.value.Property;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueStr;
import passambler.value.WriteHandler;

public class ValueResponse extends Value implements WriteHandler {
    private StringBuilder responseData = new StringBuilder();
    
    public ValueResponse(HttpContext context, HttpResponse response) {
        setProperty("Headers", new Property() {
            @Override
            public Value getValue() {
                ValueDict headers = new ValueDict();
                
                for (Header header : response.getAllHeaders()) {
                    headers.getValue().put(new ValueStr(header.getName()), new ValueStr(header.getValue()));
                }
                
                return headers;
            }
        });
    }
    
    @Override
    public void write(Value value) {
        responseData.append(value.toString());
    }
    
    public String getResponseData() {
        return responseData.toString();
    }
}