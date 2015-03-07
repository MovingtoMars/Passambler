package passambler.pack.json.function;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import passambler.function.Function;
import passambler.function.FunctionContext;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueDict;
import passambler.value.ValueList;
import passambler.value.ValueNum;
import passambler.value.ValueStr;

public class FunctionEncode extends Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueDict || value instanceof ValueList;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        return new ValueStr(((JSONAware) parse(context.getArgument(0))).toJSONString());
    }

    private Object parse(Value value) {
        if (value instanceof ValueStr) {
            return ((ValueStr) value).getValue();
        } else if (value instanceof ValueNum) {
            return ((ValueNum) value).getValue();
        } else if (value instanceof ValueList) {
            JSONArray list = new JSONArray();

            for (Value item : ((ValueList) value).getValue()) {
                list.add(parse(item));
            }

            return list;
        } else if (value instanceof ValueDict) {
            JSONObject object = new JSONObject();

            ((ValueDict) value).getValue().entrySet().stream().forEach((entry) -> {
                object.put(parse(entry.getKey()), parse(entry.getValue()));
            });

            return object;
        }

        return Value.VALUE_NIL;
    }
}
