package passambler.pack.math.function;

import java.math.BigDecimal;

public class SqrtFunction extends SimpleMathFunction {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return new BigDecimal(Math.sqrt(value.doubleValue()));
    }
}
