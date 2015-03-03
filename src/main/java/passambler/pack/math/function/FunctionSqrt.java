package passambler.pack.math.function;

import java.math.BigDecimal;

public class FunctionSqrt extends FunctionSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.sqrt(value.doubleValue()));
    }
}
