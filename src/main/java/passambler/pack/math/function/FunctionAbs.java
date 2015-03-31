package passambler.pack.math.function;

import java.math.BigDecimal;

public class FunctionAbs extends FunctionSimpleMath {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return value.abs();
    }
}
