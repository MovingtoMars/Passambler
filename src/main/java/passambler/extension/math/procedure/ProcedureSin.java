package passambler.extension.math.procedure;

import java.math.BigDecimal;

public class ProcedureSin extends ProcedureSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.sin(value.doubleValue()));
    }
}
