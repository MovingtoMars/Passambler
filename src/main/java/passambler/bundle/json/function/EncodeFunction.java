package passambler.bundle.json.function;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.BooleanValue;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;

public class EncodeFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof DictValue || value instanceof ListValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new StringValue(((JSONAware) parse(context.getArgument(0))).toJSONString());
    }

    private Object parse(Value value) {
        if (value instanceof StringValue) {
            return ((StringValue) value).toString();
        } else if (value instanceof NumberValue) {
            return ((NumberValue) value).getValue();
        } else if (value instanceof BooleanValue) {
            return ((BooleanValue) value).getValue();
        } else if (value instanceof ListValue) {
            JSONArray list = new JSONArray();

            for (Value item : ((ListValue) value).getValue()) {
                list.add(parse(item));
            }

            return list;
        } else if (value instanceof DictValue) {
            JSONObject object = new JSONObject();

            ((DictValue) value).getValue().entrySet().stream().forEach((entry) -> {
                object.put(parse(entry.getKey()), parse(entry.getValue()));
            });

            return object;
        }

        return Value.VALUE_NIL;
    }
}
