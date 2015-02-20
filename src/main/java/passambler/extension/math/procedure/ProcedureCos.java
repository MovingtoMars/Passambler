package passambler.extension.math.procedure;

import java.math.BigDecimal;

public class ProcedureCos extends ProcedureSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.cos(value.doubleValue()));
    }
}
