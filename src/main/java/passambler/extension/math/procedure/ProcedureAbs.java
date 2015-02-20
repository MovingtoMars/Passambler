package passambler.extension.math.procedure;

import java.math.BigDecimal;

public class ProcedureAbs extends ProcedureSimpleMath {
    @Override
    public BigDecimal getValue(BigDecimal value) {
        return value.abs();
    }
}
