package passambler.extension.math.procedure;

import java.math.BigDecimal;

public class ProcedureLog extends ProcedureSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return new BigDecimal(Math.log(value.doubleValue()));
    }
}
