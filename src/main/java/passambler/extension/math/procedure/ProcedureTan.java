package passambler.extension.math.procedure;

import java.math.BigDecimal;

public class ProcedureTan extends ProcedureSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.tan(value.doubleValue()));
    }
}
