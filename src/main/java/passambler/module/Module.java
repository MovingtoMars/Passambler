package passambler.module;

import java.util.Map;
import passambler.value.Value;

public interface Module {
    public String getId();

    public Module[] getChildren();

    public void apply(Map<String, Value> symbols);
}
