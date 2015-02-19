package passambler.extension.math.procedure;

public class ProcedureCos extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.cos(value);
    }
}
