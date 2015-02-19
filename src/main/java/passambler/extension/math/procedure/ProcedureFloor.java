package passambler.extension.math.procedure;

public class ProcedureFloor extends ProcedureSimpleMath {
    @Override
    public double getValue(double value) {
        return Math.floor(value);
    }
}
