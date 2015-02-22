package passambler.pkg.math.function;

import java.math.BigDecimal;

public class FunctionCos extends FunctionSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.cos(value.doubleValue()));
    }
}
