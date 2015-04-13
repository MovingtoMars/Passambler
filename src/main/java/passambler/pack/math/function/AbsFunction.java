package passambler.pack.math.function;

import java.math.BigDecimal;

public class AbsFunction extends SimpleMathFunction {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return value.abs();
    }
}
