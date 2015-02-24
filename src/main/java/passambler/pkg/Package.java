package passambler.pkg;

import java.util.Map;
import passambler.parser.Scope;
import passambler.value.Value;

public interface Package {
    public void addSymbols(Scope scope, Map<String, Value> symbols);
}
