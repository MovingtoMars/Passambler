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
        symbols.put("matches", new MatchesFunction());
        symbols.put("quote", new QuoteFunction());
        symbols.put("split", new SplitFunction());
        symbols.put("replace", new ReplaceFunction(false));
        symbols.put("replace_all", new ReplaceFunction(true));
    }
}
