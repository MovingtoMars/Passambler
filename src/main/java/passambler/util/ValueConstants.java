package passambler.util;

import passambler.value.BooleanValue;
import passambler.value.Value;

public class ValueConstants {
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);
    public static final Value NIL = new Value() {
        @Override
        public String toString() {
            return "nil";
        }
    };
    public static final Value STOP = new Value();
    public static final Value SKIP = new Value();
}
