package passambler.pack.math.function;

import java.math.BigDecimal;

public class FunctionTan extends FunctionSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.tan(value.doubleValue()));
    }
}
