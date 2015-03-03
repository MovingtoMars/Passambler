package passambler.pack.math.function;

import java.math.BigDecimal;

public class FunctionSin extends FunctionSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.sin(value.doubleValue()));
    }
}
