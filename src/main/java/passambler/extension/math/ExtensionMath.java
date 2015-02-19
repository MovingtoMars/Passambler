package passambler.extension.math;

import passambler.extension.Extension;
import passambler.extension.math.procedure.*;
import passambler.parser.Scope;
import passambler.value.ValueNum;

public class ExtensionMath implements Extension {
    @Override
    public String getId() {
        return "math";
    }
    
    @Override
    public void applySymbols(Scope scope) {
        scope.setSymbol("sin", new ProcedureSin());
        scope.setSymbol("cos", new ProcedureCos());
        scope.setSymbol("tan", new ProcedureTan());
        scope.setSymbol("abs", new ProcedureAbs());
        scope.setSymbol("ceil", new ProcedureCeil());
        scope.setSymbol("floor", new ProcedureFloor());
        scope.setSymbol("sqrt", new ProcedureSqrt());
        scope.setSymbol("log", new ProcedureLog());
        scope.setSymbol("log10", new ProcedureLog10());
        scope.setSymbol("random", new ProcedureRandom());
        
        scope.setSymbol("pi", new ValueNum(Math.PI));
    }
}
