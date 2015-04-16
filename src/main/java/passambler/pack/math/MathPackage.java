package passambler.pack.math;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.math.function.*;
import passambler.value.Value;
import passambler.value.NumberValue;

public class MathPackage implements Package {
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
        symbols.put("Sin", new SinFunction());
        symbols.put("Cos", new CosFunction());
        symbols.put("Tan", new TanFunction());
        symbols.put("Abs", new AbsFunction());
        symbols.put("Ceil", new CeilFunction());
        symbols.put("Floor", new FloorFunction());
        symbols.put("Sqrt", new SqrtFunction());
        symbols.put("Log", new LogFunction());
        symbols.put("Log10", new Log10Function());
        symbols.put("Rand", new RandFunction());

        symbols.put("Pi", new NumberValue(Math.PI));
        symbols.put("E", new NumberValue(Math.E));
    }
}
