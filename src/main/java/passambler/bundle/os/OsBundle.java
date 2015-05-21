package passambler.bundle.os;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.os.function.*;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.Value;
import passambler.value.StringValue;

public class OsBundle implements Bundle {
    public static ListValue args = new ListValue();

    @Override
    public String getId() {
        return "os";
    }

    @Override
    public Bundle[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("exit", new ExitFunction());
        symbols.put("time", new TimeFunction());
        symbols.put("exec", new ExecFunction());

        symbols.put("line_separator", new StringValue(System.getProperty("line.separator")));
        symbols.put("file_separator", new StringValue(System.getProperty("file.separator")));
        symbols.put("path_separator", new StringValue(System.getProperty("path.separator")));

        symbols.put("user_dir", new StringValue(System.getProperty("user.dir")));
        symbols.put("user_home", new StringValue(System.getProperty("user.home")));
        symbols.put("user_name", new StringValue(System.getProperty("user.name")));

        symbols.put("name", new StringValue(System.getProperty("os.name")));
        symbols.put("version", new StringValue(System.getProperty("os.version")));
        symbols.put("arch", new StringValue(System.getProperty("os.arch")));

        symbols.put("env", getEnvDict());

        symbols.put("args", args);
    }

    private DictValue getEnvDict() {
        DictValue value = new DictValue();

        System.getenv().entrySet().stream().forEach((entry) -> {
            value.setEntry(new StringValue(entry.getKey()), new StringValue(entry.getValue()));
        });

        return value;
    }
}
