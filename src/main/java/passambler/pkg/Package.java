package passambler.pkg;

import java.util.Map;
import passambler.value.Value;

public interface Package {
    public void addSymbols(Map<String, Value> symbols);
}
