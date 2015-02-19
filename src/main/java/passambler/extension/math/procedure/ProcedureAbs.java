package passambler.extension.math.procedure;

public class ProcedureAbs extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.abs(value);
    }
}
