package passambler.extension.math.procedure;

public class ProcedureLog10 extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.log10(value);
    }
}
