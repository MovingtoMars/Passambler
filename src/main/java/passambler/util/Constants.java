package passambler.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import passambler.value.BooleanValue;
import passambler.value.Value;

public class Constants {
    public static final String VERSION = "DEV";

    public static final Logger LOGGER = LogManager.getLogger("Passambler");

    public static final BooleanValue VALUE_TRUE = new BooleanValue(true);
    public static final BooleanValue VALUE_FALSE = new BooleanValue(false);
    public static final Value VALUE_NIL = new Value() {
        @Override
        public String toString() {
            return "nil";
        }
    };
}
