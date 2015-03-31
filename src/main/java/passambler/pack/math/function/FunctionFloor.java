package passambler.pack.math.function;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FunctionFloor extends FunctionSimpleMath {
    @Override
    public BigDecimal getReturnValue(BigDecimal value) {
        return value.setScale(0, RoundingMode.FLOOR);
    }
}
