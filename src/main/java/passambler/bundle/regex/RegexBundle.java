package passambler.bundle.regex;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.regex.function.*;
import passambler.value.Value;

public class RegexBundle implements Bundle {
    @Override
    public String getId() {
        return "regex";
    }

    @Override
    public Bundle[] getChildren() {
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
