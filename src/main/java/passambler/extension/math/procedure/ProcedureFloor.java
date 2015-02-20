package passambler.extension.math.procedure;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProcedureFloor extends ProcedureSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return value.setScale(0, RoundingMode.FLOOR);
    }
}
