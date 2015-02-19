package passambler.extension.std.procedure;

import passambler.procedure.ProcedureSimple;
import passambler.value.Value;
import passambler.value.ValueNum;

public class ProcedureMicrotime extends ProcedureSimple {
    @Override
    public Value getValue() {
        return new ValueNum(System.currentTimeMillis());
    }
}
