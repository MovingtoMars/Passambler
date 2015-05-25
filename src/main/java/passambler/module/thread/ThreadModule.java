package passambler.module.thread;

import java.util.Map;
import passambler.module.Module;
import passambler.module.thread.function.*;
import passambler.value.Value;

public class ThreadModule implements Module {
    @Override
    public String getId() {
        return "thread";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("start", new StartFunction());
        symbols.put("sleep", new SleepFunction());
    }
}
