package passambler.module.math.function;

import java.math.BigDecimal;

public class LogFunction extends SimpleMathFunction {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return new BigDecimal(Math.log(value.doubleValue()));
    }
}
