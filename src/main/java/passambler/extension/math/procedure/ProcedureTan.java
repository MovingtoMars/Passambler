package passambler.extension.math.procedure;

public class ProcedureTan extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.tan(value);
    }
}
