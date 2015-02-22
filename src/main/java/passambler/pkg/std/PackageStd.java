package passambler.pkg.std;

import java.util.Map;
import passambler.pkg.Package;
import passambler.pkg.std.function.*;
import passambler.pkg.std.function.FunctionRead.ReadType;
import passambler.parser.Scope;
import passambler.pkg.std.value.*;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNil;

public class PackageStd implements Package {
    public static final ValueNil VALUE_NIL = new ValueNil();

    @Override
    public String getId() {
        return "std";
    }

    @Override
    public void addSymbols(Scope scope, Map<String, Value> symbols) {
        scope.setSymbol("write", new FunctionWrite(false));
        scope.setSymbol("writeln", new FunctionWrite(true));
        scope.setSymbol("readstr", new FunctionRead(ReadType.STRING));
        scope.setSymbol("readint", new FunctionRead(ReadType.INTEGER));
        scope.setSymbol("readdouble", new FunctionRead(ReadType.DOUBLE));
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

        scope.setSymbol("nil", VALUE_NIL);
        scope.setSymbol("true", new ValueBool(true));
        scope.setSymbol("false", new ValueBool(false));
        scope.setSymbol("stdout", new ValueOutStream(System.out));
        scope.setSymbol("stderr", new ValueOutStream(System.err));
        scope.setSymbol("stdin", new ValueInStream(System.in));
    }
}
