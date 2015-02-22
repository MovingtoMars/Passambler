package passambler.pkg.std.function;

import passambler.function.FunctionSimple;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionMicrotime extends FunctionSimple {
    @Override
    public Value getValue() {
        return new ValueNum(System.currentTimeMillis());
    }
}
