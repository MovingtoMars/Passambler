package passambler.extension.math.procedure;

public class ProcedureCeil extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.ceil(value);
    }
}
