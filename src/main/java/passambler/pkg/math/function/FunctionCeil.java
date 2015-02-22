package passambler.pkg.math.function;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FunctionCeil extends FunctionSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return value.setScale(0, RoundingMode.CEILING);
    }
}
