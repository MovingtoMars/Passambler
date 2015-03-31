package passambler.pack.json.function;

import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class FunctionDecode extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return parse(JSONValue.parse(((ValueStr) context.getArgument(0)).getValue()));
    }
    
    public Value parse(Object object) {
        if (object instanceof JSONArray) {
            ValueList list = new ValueList();
            
            for (Object item : (JSONArray) object) {
                list.getValue().add(parse(item));
            }
            
            return list;
        } else if (object instanceof JSONObject) {
            ValueDict dict = new ValueDict();
            
            for (Object item : ((JSONObject) object).entrySet()) {
               Map.Entry entry = (Map.Entry) item;
               
               dict.getValue().put(parse(entry.getKey()), parse(entry.getValue()));
            }
            
            return dict;
        } else if (object instanceof String) {
            return new ValueStr(String.valueOf(object));
        } else if (object instanceof Long) {
            return new ValueNum((Long) object);
        } else if (object instanceof Double) {
            return new ValueNum((Double) object);
        } else if (object instanceof Boolean) {
            return new ValueBool((Boolean) object);
        }
        
        return Value.VALUE_NIL;
    }
}
