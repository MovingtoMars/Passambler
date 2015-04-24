package passambler.bundle.std;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.std.function.*;
import passambler.bundle.std.value.*;
import passambler.value.BooleanValue;
import passambler.value.CharacterValue;
import passambler.value.ClassValue;
import passambler.value.DictValue;
import passambler.value.ErrorValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class StdBundle implements Bundle {
    @Override
    public String getId() {
        return "std";
    }

    @Override
    public Bundle[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("ToStr", new ToStrFunction());
        symbols.put("ToNum", new ToNumFunction());
        symbols.put("ToBool", new ToBoolFunction());
        symbols.put("ToCharacter", new ToCharacterFunction());
        symbols.put("IsStr", new IsFunction(StringValue.class));
        symbols.put("IsNum", new IsFunction(NumberValue.class));
        symbols.put("IsBool", new IsFunction(BooleanValue.class));
        symbols.put("IsList", new IsFunction(ListValue.class));
        symbols.put("IsDict", new IsFunction(DictValue.class));
        symbols.put("IsClass", new IsFunction(ClassValue.class));
        symbols.put("IsCharacter", new IsFunction(CharacterValue.class));
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
        symbols.put("Join", new JoinFunction());
        symbols.put("Filter", new FilterFunction());
        symbols.put("Substr", new SubstrFunction());

        symbols.put("Out", new OutValue());
        symbols.put("In", new InValue());
        symbols.put("Err", new ErrValue());
    }
}
