package passambler.extension.math.procedure;

public class ProcedureLog extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.log(value);
    }
}
