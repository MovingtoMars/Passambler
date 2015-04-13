package passambler.pack.math.function;

import java.math.BigDecimal;

public class Log10Function extends SimpleMathFunction {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return new BigDecimal(Math.log10(value.doubleValue()));
    }
}
