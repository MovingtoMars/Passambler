package passambler.pack.std;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.std.function.ContainsFunction;
import passambler.pack.std.function.EmptyFunction;
import passambler.pack.std.function.EntriesFunction;
import passambler.pack.std.function.EvalFunction;
import passambler.pack.std.function.FilterFunction;
import passambler.pack.std.function.FirstFunction;
import passambler.pack.std.function.IndexOfFunction;
import passambler.pack.std.function.IsFunction;
import passambler.pack.std.function.JoinFunction;
import passambler.pack.std.function.KeysFunction;
import passambler.pack.std.function.LCaseFunction;
import passambler.pack.std.function.LastFunction;
import passambler.pack.std.function.ListFunction;
import passambler.pack.std.function.PopFunction;
import passambler.pack.std.function.PushFunction;
import passambler.pack.std.function.ReadFunction;
import passambler.pack.std.function.ReplaceFunction;
import passambler.pack.std.function.ReverseFunction;
import passambler.pack.std.function.ShiftFunction;
import passambler.pack.std.function.SizeFunction;
import passambler.pack.std.function.SliceFunction;
import passambler.pack.std.function.SplitFunction;
import passambler.pack.std.function.SubstrFunction;
import passambler.pack.std.function.ToBoolFunction;
import passambler.pack.std.function.ToNumFunction;
import passambler.pack.std.function.ToStrFunction;
import passambler.pack.std.function.UCaseFunction;
import passambler.pack.std.function.ValuesFunction;
import passambler.pack.std.function.WriteFunction;
import passambler.pack.std.value.ErrValue;
import passambler.pack.std.value.InValue;
import passambler.pack.std.value.OutValue;
import passambler.value.BooleanValue;
import passambler.value.ClassValue;
import passambler.value.DictValue;
import passambler.value.ErrorValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;
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
        symbols.put("IsStr", new IsFunction(StringValue.class));
        symbols.put("IsNum", new IsFunction(NumberValue.class));
        symbols.put("IsBool", new IsFunction(BooleanValue.class));
        symbols.put("IsList", new IsFunction(ListValue.class));
        symbols.put("IsDict", new IsFunction(DictValue.class));
        symbols.put("IsClass", new IsFunction(ClassValue.class));
        symbols.put("IsErr", new IsFunction(ErrorValue.class));
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
