package passambler.extension.std;

import java.util.Map;
import passambler.extension.Extension;
import passambler.extension.std.procedure.*;
import passambler.extension.std.procedure.ProcedureRead.ReadType;
import passambler.extension.std.value.*;
import passambler.parser.Scope;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNil;

public class ExtensionStd implements Extension {
    public static final ValueNil VALUE_NIL = new ValueNil();
    
    @Override
    public String getId() {
        return "std";
    }
    
    @Override
    public void addSymbols(Scope scope, Map<String, Value> symbols) {
        scope.setSymbol("write", new ProcedureWrite(false));
        scope.setSymbol("writeln", new ProcedureWrite(true));
        scope.setSymbol("readstr", new ProcedureRead(ReadType.STRING));
        scope.setSymbol("readint", new ProcedureRead(ReadType.INTEGER));
        scope.setSymbol("readdouble", new ProcedureRead(ReadType.DOUBLE));
        scope.setSymbol("exit", new ProcedureExit());
        scope.setSymbol("microtime", new ProcedureMicrotime());
        scope.setSymbol("str", new ProcedureStr());
        scope.setSymbol("lcase", new ProcedureLCase());
        scope.setSymbol("ucase", new ProcedureUCase());
        scope.setSymbol("contains", new ProcedureContains());
        scope.setSymbol("indexof", new ProcedureIndexOf());
        scope.setSymbol("replace", new ProcedureReplace());
        scope.setSymbol("split", new ProcedureSplit());
        scope.setSymbol("first", new ProcedureFirst());
        scope.setSymbol("last", new ProcedureLast());
        scope.setSymbol("size", new ProcedureSize());
        scope.setSymbol("empty", new ProcedureEmpty());
        scope.setSymbol("push", new ProcedurePush());
        scope.setSymbol("pop", new ProcedurePop());
        scope.setSymbol("shift", new ProcedureShift());
        scope.setSymbol("slice", new ProcedureSlice());
        scope.setSymbol("reverse", new ProcedureReverse());
        scope.setSymbol("keys", new ProcedureKeys());
        scope.setSymbol("values", new ProcedureValues());
        scope.setSymbol("entries", new ProcedureEntries());

        scope.setSymbol("nil", VALUE_NIL);
        scope.setSymbol("true", new ValueBool(true));
        scope.setSymbol("false", new ValueBool(false));
        scope.setSymbol("stdout", new ValueOutStream(System.out));
        scope.setSymbol("stderr", new ValueOutStream(System.err));
        scope.setSymbol("stdin", new ValueInStream(System.in));
    }
}
