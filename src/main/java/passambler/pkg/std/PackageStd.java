package passambler.pkg.std;

import java.util.Map;
import passambler.pkg.Package;
import passambler.pkg.std.function.*;
import passambler.pkg.std.value.*;
import passambler.value.Value;

public class PackageStd implements Package {
    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Write", new FunctionWrite(new ValueStdOut(), false));
        symbols.put("Writeln", new FunctionWrite(new ValueStdOut(), true));
        symbols.put("Str", new FunctionStr());
        symbols.put("Lcase", new FunctionLCase());
        symbols.put("Ucase", new FunctionUCase());
        symbols.put("Contains", new FunctionContains());
        symbols.put("IndexOf", new FunctionIndexOf());
        symbols.put("Replace", new FunctionReplace());
        symbols.put("Split", new FunctionSplit());
        symbols.put("First", new FunctionFirst());
        symbols.put("Last", new FunctionLast());
        symbols.put("Size", new FunctionSize());
        symbols.put("Empty", new FunctionEmpty());
        symbols.put("Push", new FunctionPush());
        symbols.put("Pop", new FunctionPop());
        symbols.put("Shift", new FunctionShift());
        symbols.put("Slice", new FunctionSlice());
        symbols.put("Reverse", new FunctionReverse());
        symbols.put("Keys", new FunctionKeys());
        symbols.put("Values", new FunctionValues());
        symbols.put("Entries", new FunctionEntries());
        symbols.put("Eval", new FunctionEval());

        symbols.put("Stdout", new ValueStdOut());
        symbols.put("Stderr", new ValueStdErr());
    }
}
