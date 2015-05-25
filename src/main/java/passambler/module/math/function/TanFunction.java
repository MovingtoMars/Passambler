package passambler.module.math.function;

import java.math.BigDecimal;

public class TanFunction extends SimpleMathFunction {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return new BigDecimal(Math.tan(value.doubleValue()));
    }
}
