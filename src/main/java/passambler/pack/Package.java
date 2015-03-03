package passambler.pack;

import java.util.Map;
import passambler.value.Value;

public interface Package {
    public String getId();

    public Package[] getChildren();

    public void apply(Map<String, Value> symbols);
}
