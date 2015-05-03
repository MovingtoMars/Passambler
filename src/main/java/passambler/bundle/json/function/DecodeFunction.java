package passambler.bundle.json.function;

import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import passambler.exception.EngineException;
import passambler.util.ValueUtil;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.StringValue;

public class DecodeFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return parse(JSONValue.parse(((StringValue) context.getArgument(0)).toString()));
    }

    public Value parse(Object object) {
        if (object instanceof JSONArray) {
            ListValue list = new ListValue();

            for (Object item : (JSONArray) object) {
                list.getValue().add(parse(item));
            }

            return list;
        } else if (object instanceof JSONObject) {
            DictValue dict = new DictValue();

            for (Object item : ((JSONObject) object).entrySet()) {
                Map.Entry entry = (Map.Entry) item;

                dict.getValue().put(parse(entry.getKey()), parse(entry.getValue()));
            }

            return dict;
        }

        return ValueUtil.toValue(object);
    }
}
