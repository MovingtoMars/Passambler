package passambler.extension.math.procedure;

import java.math.BigDecimal;

public class ProcedureLog10 extends ProcedureSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.log10(value.doubleValue()));
    }
}
