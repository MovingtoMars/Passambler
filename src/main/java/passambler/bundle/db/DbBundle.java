package passambler.bundle.db;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.value.Value;

public class DbBundle implements Bundle {
    @Override
    public String getId() {
        return "db";
    }

    @Override
    public Bundle[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
    }
}
