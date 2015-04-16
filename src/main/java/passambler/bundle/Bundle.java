package passambler.bundle;

import java.util.Map;
import passambler.value.Value;

public interface Bundle {
    public String getId();

    public Bundle[] getChildren();

    public void apply(Map<String, Value> symbols);
}
