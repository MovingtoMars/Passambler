package passambler.module.std;

import java.util.Map;
import passambler.module.Module;
import passambler.module.std.function.*;
import passambler.module.std.value.*;
import passambler.value.BooleanValue;
import passambler.value.CharacterValue;
import passambler.value.DictValue;
import passambler.value.ErrorValue;
import passambler.value.ListValue;
import passambler.value.NumberValue;
import passambler.value.StringValue;
import passambler.value.Value;

public class StdModule implements Module {
    @Override
    public String getId() {
        return "std";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("to_str", new ToStrFunction());
        symbols.put("to_num", new ToNumFunction());
        symbols.put("to_bool", new ToBoolFunction());
        symbols.put("to_char", new ToCharFunction());
        symbols.put("is_str", new IsFunction(StringValue.class));
        symbols.put("is_num", new IsFunction(NumberValue.class));
        symbols.put("is_bool", new IsFunction(BooleanValue.class));
        symbols.put("is_list", new IsFunction(ListValue.class));
        symbols.put("is_dict", new IsFunction(DictValue.class));
        symbols.put("is_char", new IsFunction(CharacterValue.class));
        symbols.put("is_err", new IsFunction(ErrorValue.class));
        symbols.put("lcase", new LCaseFunction());
        symbols.put("ucase", new UCaseFunction());
        symbols.put("contains", new ContainsFunction());
        symbols.put("index_of", new IndexOfFunction());
        symbols.put("replace", new ReplaceFunction());
        symbols.put("split", new SplitFunction());
        symbols.put("first", new FirstFunction());
        symbols.put("last", new LastFunction());
        symbols.put("size", new SizeFunction());
        symbols.put("empty", new EmptyFunction());
        symbols.put("push", new PushFunction());
        symbols.put("pop", new PopFunction());
        symbols.put("delete", new DeleteFunction());
        symbols.put("shift", new ShiftFunction());
        symbols.put("reverse", new ReverseFunction());
        symbols.put("keys", new KeysFunction());
        symbols.put("values", new ValuesFunction());
        symbols.put("entries", new EntriesFunction());
        symbols.put("eval", new EvalFunction());
        symbols.put("tokenize", new TokenizeFunction());
        symbols.put("join", new JoinFunction());
        symbols.put("filter", new FilterFunction());
        symbols.put("substr", new SubstrFunction());
        symbols.put("format", new FormatFunction());

        symbols.put("out", new OutValue());
        symbols.put("in", new InValue());
        symbols.put("err", new ErrValue());
    }
}
