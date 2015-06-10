package passambler.module.regex;

import java.util.Map;
import passambler.module.Module;
import passambler.module.regex.function.*;
import passambler.value.Value;

public class RegexModule implements Module {
    @Override
    public String getId() {
        return "regex";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Matches", new MatchesFunction());
        symbols.put("Quote", new QuoteFunction());
        symbols.put("Split", new SplitFunction());
        symbols.put("Replace", new ReplaceFunction(false));
        symbols.put("ReplaceAll", new ReplaceFunction(true));
    }
}
