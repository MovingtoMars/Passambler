package passambler.pack.math;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.math.function.*;
import passambler.value.Value;
import passambler.value.NumberValue;

public class PackageMath implements Package {
    @Override
    public String getId() {
        return "math";
    }

    @Override
    public Package[] getChildren() {
        return null;
    }
    
    @Override
    public void apply(Map<String, Value> symbols) {
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

        symbols.put("Pi", new NumberValue(Math.PI));
        symbols.put("E", new NumberValue(Math.E));
    }
}
