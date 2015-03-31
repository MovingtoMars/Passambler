package passambler.pack.os.function;

import java.time.Instant;
import passambler.value.function.FunctionSimple;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionTime extends FunctionSimple {
    @Override
    public Value getReturnValue() {
        return new ValueNum(Instant.now().getEpochSecond());
    }
}
