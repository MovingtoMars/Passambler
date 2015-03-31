package passambler.pack.math.function;

import java.math.BigDecimal;

public class FunctionLog extends FunctionSimpleMath {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return new BigDecimal(Math.log(value.doubleValue()));
    }
}
