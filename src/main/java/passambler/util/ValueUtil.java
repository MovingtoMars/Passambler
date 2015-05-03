package passambler.util;

import java.util.Date;
import passambler.value.BooleanValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class ValueUtil {
    public static Value toValue(Object object) {
        if (object instanceof String || object instanceof Date) {
            return new StringValue(String.valueOf(object));
        } else if (object instanceof Long) {
            return new NumberValue((Long) object);
        } else if (object instanceof Double) {
            return new NumberValue((Double) object);
        } else if (object instanceof Integer) {
            return new NumberValue((Integer) object);
        } else if (object instanceof Boolean) {
            return new BooleanValue((Boolean) object);
        }

        return Constants.VALUE_NIL;
    }

    public static boolean compare(Value value1, Value value2) {
        if (value1.getValue() == null && value2.getValue() == null) {
            return true;
        }

        if ((value1.getValue() != null && value2.getValue() == null) || (value1.getValue() == null && value2.getValue() != null)) {
            return false;
        }

        return value1.getValue().equals(value2.getValue());
    }
}
