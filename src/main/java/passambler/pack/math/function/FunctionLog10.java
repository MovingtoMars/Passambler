package passambler.pack.math.function;

import java.math.BigDecimal;

public class FunctionLog10 extends FunctionSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.log10(value.doubleValue()));
    }
}
