package passambler.pack.std;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.std.function.*;
import passambler.pack.std.value.*;
import passambler.value.Value;

public class StdPackage implements Package {
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
        symbols.put("Write", new WriteFunction(new OutValue(), false));
        symbols.put("Writeln", new WriteFunction(new OutValue(), true));
        symbols.put("Read", new ReadFunction());
        symbols.put("ToStr", new ToStrFunction());
        symbols.put("ToNum", new ToNumFunction());
        symbols.put("ToBool", new ToBoolFunction());
        symbols.put("Lcase", new LCaseFunction());
        symbols.put("Ucase", new UCaseFunction());
        symbols.put("Contains", new ContainsFunction());
        symbols.put("IndexOf", new IndexOfFunction());
        symbols.put("Replace", new ReplaceFunction());
        symbols.put("Split", new SplitFunction());
        symbols.put("First", new FirstFunction());
        symbols.put("Last", new LastFunction());
        symbols.put("Size", new SizeFunction());
        symbols.put("Empty", new EmptyFunction());
        symbols.put("Push", new PushFunction());
        symbols.put("Pop", new PopFunction());
        symbols.put("Shift", new ShiftFunction());
        symbols.put("Slice", new SliceFunction());
        symbols.put("Reverse", new ReverseFunction());
        symbols.put("Keys", new KeysFunction());
        symbols.put("Values", new ValuesFunction());
        symbols.put("Entries", new EntriesFunction());
        symbols.put("Eval", new EvalFunction());
        symbols.put("List", new ListFunction());
        symbols.put("Join", new JoinFunction());
        symbols.put("Filter", new FilterFunction());
        symbols.put("Substr", new SubstrFunction());

        symbols.put("Out", new OutValue());
        symbols.put("In", new InValue());
        symbols.put("Err", new ErrValue());
    }
}
