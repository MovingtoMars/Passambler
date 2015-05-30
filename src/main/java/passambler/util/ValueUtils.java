package passambler.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import passambler.value.BooleanValue;
import passambler.value.CharacterValue;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class ValueUtils {
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

        return ValueConstants.NIL;
    }

    public static boolean compare(Value value1, Value value2) {
        if (value1.getValue() == null && value2.getValue() == null) {
            return true;
        }

        if ((value1.getValue() != null && value2.getValue() == null) || (value1.getValue() == null && value2.getValue() != null)) {
            return false;
        }

        if ((value1 instanceof StringValue || value1 instanceof CharacterValue) && (value2 instanceof StringValue || value2 instanceof CharacterValue)) {
            return value1.toString().equals(value2.toString());
        }

        if (value1 instanceof ListValue && value2 instanceof ListValue) {
            ListValue list1 = (ListValue) value1;
            ListValue list2 = (ListValue) value2;

            if (list1.getValue().size() != list2.getValue().size()) {
                return false;
            }

            for (int i = 0; i < list1.getValue().size(); ++i) {
                if (!list1.getValue().get(i).equals(list2.getValue().get(i))) {
                    return false;
                }
            }

            return true;
        }

        if (value1 instanceof DictValue && value2 instanceof DictValue) {
            List values1 = new ArrayList<>(((DictValue) value1).getValue().values());
            List values2 = new ArrayList<>(((DictValue) value2).getValue().values());

            if (!values1.equals(values2)) {
                return false;
            } else {
                List keys1 = new ArrayList<>(((DictValue) value1).getValue().keySet());
                List keys2 = new ArrayList<>(((DictValue) value2).getValue().keySet());

                return keys1.equals(keys2);
            }
        }

        return value1.getValue().equals(value2.getValue());
    }
}
