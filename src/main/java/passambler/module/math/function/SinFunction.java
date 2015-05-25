package passambler.module.math.function;

import java.math.BigDecimal;

public class SinFunction extends SimpleMathFunction {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return new BigDecimal(Math.sin(value.doubleValue()));
    }
}
