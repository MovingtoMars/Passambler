package passambler.pkg.std;

import java.util.Map;
import passambler.pkg.Package;
import passambler.pkg.std.function.*;
import passambler.pkg.std.value.*;
import passambler.parser.Scope;
import passambler.value.Value;

public class PackageStd implements Package {
    @Override
    public void addSymbols(Scope scope, Map<String, Value> symbols) {
        scope.setSymbol("Write", new FunctionWrite(new ValueStdOut(), false));
        scope.setSymbol("Writeln", new FunctionWrite(new ValueStdOut(), true));
        scope.setSymbol("Exit", new FunctionExit());
        scope.setSymbol("Time", new FunctionTime());
        scope.setSymbol("Str", new FunctionStr());
        scope.setSymbol("Lcase", new FunctionLCase());
        scope.setSymbol("Ucase", new FunctionUCase());
        scope.setSymbol("Contains", new FunctionContains());
        scope.setSymbol("IndexOf", new FunctionIndexOf());
        scope.setSymbol("Replace", new FunctionReplace());
        scope.setSymbol("Split", new FunctionSplit());
        scope.setSymbol("First", new FunctionFirst());
        scope.setSymbol("Last", new FunctionLast());
        scope.setSymbol("Size", new FunctionSize());
        scope.setSymbol("Empty", new FunctionEmpty());
        scope.setSymbol("Push", new FunctionPush());
        scope.setSymbol("Pop", new FunctionPop());
        scope.setSymbol("Shift", new FunctionShift());
        scope.setSymbol("Slice", new FunctionSlice());
        scope.setSymbol("Reverse", new FunctionReverse());
        scope.setSymbol("Keys", new FunctionKeys());
        scope.setSymbol("Values", new FunctionValues());
        scope.setSymbol("Entries", new FunctionEntries());

        scope.setSymbol("Stdout", new ValueStdOut());
        scope.setSymbol("Stderr", new ValueStdErr());
    }
}
