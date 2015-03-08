package passambler.pack.std;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.std.function.*;
import passambler.pack.std.value.*;
import passambler.value.Value;

public class PackageStd implements Package {
    @Override
    public String getId() {
        return "std";
    }

    @Override
    public Package[] getChildren() {
        return null;
    }
    
    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Write", new FunctionWrite(new ValueOut(), false));
        symbols.put("Writeln", new FunctionWrite(new ValueOut(), true));
        symbols.put("Read", new FunctionRead());
        symbols.put("ReadNum", new FunctionReadNum());
        symbols.put("ReadStr", new FunctionReadStr());
        symbols.put("Str", new FunctionStr());
        symbols.put("Num", new FunctionNum());
        symbols.put("Bool", new FunctionBool());
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
        symbols.put("List", new FunctionList());
        symbols.put("Join", new FunctionJoin());
        symbols.put("Filter", new FunctionFilter());

        symbols.put("Out", new ValueOut());
        symbols.put("Err", new ValueErr());
    }
}