package passambler.extension.math.procedure;

public class ProcedureSin extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.sin(value);
    }
}
