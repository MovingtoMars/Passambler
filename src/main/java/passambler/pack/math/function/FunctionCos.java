package passambler.pack.math.function;

import java.math.BigDecimal;

public class FunctionCos extends FunctionSimpleMath {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return new BigDecimal(Math.cos(value.doubleValue()));
    }
}
