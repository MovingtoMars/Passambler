package passambler.bundle.regex;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.regex.function.PatternFunction;
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
        symbols.put("Pattern", new PatternFunction());
    }
}
