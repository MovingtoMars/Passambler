package passambler.extension.math;

import java.util.Map;
import passambler.extension.Extension;
import passambler.extension.math.procedure.*;
import passambler.parser.Scope;
import passambler.value.Value;
import passambler.value.ValueNum;

public class ExtensionMath implements Extension {
    @Override
    public String getId() {
        return "math";
    }
    
    @Override
    public void addSymbols(Scope scope, Map<String, Value> symbols) {
        symbols.put("sin", new ProcedureSin());
        symbols.put("cos", new ProcedureCos());
        symbols.put("tan", new ProcedureTan());
        symbols.put("abs", new ProcedureAbs());
        symbols.put("ceil", new ProcedureCeil());
        symbols.put("floor", new ProcedureFloor());
        symbols.put("sqrt", new ProcedureSqrt());
        symbols.put("log", new ProcedureLog());
        symbols.put("log10", new ProcedureLog10());
        symbols.put("random", new ProcedureRandom());
        
        symbols.put("pi", new ValueNum(Math.PI));
    }
}
