package passambler.pkg.math;

import java.util.Map;
import passambler.pkg.Package;
import passambler.pkg.math.function.*;
import passambler.value.Value;
import passambler.value.ValueNum;

public class PackageMath implements Package {
    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Sin", new FunctionSin());
        symbols.put("Cos", new FunctionCos());
        symbols.put("Tan", new FunctionTan());
        symbols.put("Abs", new FunctionAbs());
        symbols.put("Ceil", new FunctionCeil());
        symbols.put("Floor", new FunctionFloor());
        symbols.put("Sqrt", new FunctionSqrt());
        symbols.put("Log", new FunctionLog());
        symbols.put("Log10", new FunctionLog10());
        symbols.put("Rand", new FunctionRand());

        symbols.put("Pi", new ValueNum(Math.PI));
        symbols.put("E", new ValueNum(Math.E));
    }
}
