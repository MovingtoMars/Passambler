package passambler.bundle.math.function;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FloorFunction extends SimpleMathFunction {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return value.setScale(0, RoundingMode.FLOOR);
    }
}
