package passambler.extension;

import java.util.Map;
import passambler.parser.Scope;
import passambler.value.Value;

public interface Extension {
    public String getId();
    
    public void addSymbols(Scope scope, Map<String, Value> symbols);
}
