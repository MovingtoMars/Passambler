package passambler.extension.math;

import java.util.Map;
import passambler.extension.math.function.*;
import passambler.extension.Extension;
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
        symbols.put("sin", new FunctionSin());
        symbols.put("cos", new FunctionCos());
        symbols.put("tan", new FunctionTan());
        symbols.put("abs", new FunctionAbs());
        symbols.put("ceil", new FunctionCeil());
        symbols.put("floor", new FunctionFloor());
        symbols.put("sqrt", new FunctionSqrt());
        symbols.put("log", new FunctionLog());
        symbols.put("log10", new FunctionLog10());
        symbols.put("random", new FunctionRandom());

        symbols.put("pi", new ValueNum(Math.PI));
    }
}
