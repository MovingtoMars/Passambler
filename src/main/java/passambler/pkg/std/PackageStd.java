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
        scope.setSymbol("write", new FunctionWrite(new ValueStdOut(), false));
        scope.setSymbol("writeln", new FunctionWrite(new ValueStdOut(), true));
        scope.setSymbol("exit", new FunctionExit());
        scope.setSymbol("microtime", new FunctionMicrotime());
        scope.setSymbol("str", new FunctionStr());
        scope.setSymbol("lcase", new FunctionLCase());
        scope.setSymbol("ucase", new FunctionUCase());
        scope.setSymbol("contains", new FunctionContains());
        scope.setSymbol("indexof", new FunctionIndexOf());
        scope.setSymbol("replace", new FunctionReplace());
        scope.setSymbol("split", new FunctionSplit());
        scope.setSymbol("first", new FunctionFirst());
        scope.setSymbol("last", new FunctionLast());
        scope.setSymbol("size", new FunctionSize());
        scope.setSymbol("empty", new FunctionEmpty());
        scope.setSymbol("push", new FunctionPush());
        scope.setSymbol("pop", new FunctionPop());
        scope.setSymbol("shift", new FunctionShift());
        scope.setSymbol("slice", new FunctionSlice());
        scope.setSymbol("reverse", new FunctionReverse());
        scope.setSymbol("keys", new FunctionKeys());
        scope.setSymbol("values", new FunctionValues());
        scope.setSymbol("entries", new FunctionEntries());

        scope.setSymbol("stdout", new ValueStdOut());
        scope.setSymbol("stderr", new ValueStdErr());
    }
}
