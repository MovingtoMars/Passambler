package passambler.pkg.os.function;

import passambler.function.FunctionSimple;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionTime extends FunctionSimple {
    @Override
    public Value getValue() {
        return new ValueNum((int) (System.currentTimeMillis() / 1000L));
    }
}
