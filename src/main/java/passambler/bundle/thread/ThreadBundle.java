package passambler.bundle.thread;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.thread.function.*;
import passambler.value.Value;

public class ThreadBundle implements Bundle {
    @Override
    public String getId() {
        return "thread";
    }

    @Override
    public Bundle[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Start", new StartFunction());
        symbols.put("Sleep", new SleepFunction());
    }
}
