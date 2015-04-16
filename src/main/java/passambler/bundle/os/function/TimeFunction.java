package passambler.bundle.os.function;

import java.time.Instant;
import passambler.value.function.SimpleFunction;
import passambler.value.Value;
import passambler.value.NumberValue;

public class TimeFunction extends SimpleFunction {
    @Override
    public Value getReturnValue() {
        return new NumberValue(Instant.now().getEpochSecond());
    }
}
