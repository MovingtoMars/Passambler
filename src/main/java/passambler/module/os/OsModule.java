package passambler.module.os;

import java.util.Map;
import passambler.module.Module;
import passambler.module.os.function.*;
import passambler.value.DictValue;
import passambler.value.ListValue;
import passambler.value.Value;
import passambler.value.StringValue;

public class OsModule implements Module {
    public static ListValue args = new ListValue();

    @Override
    public String getId() {
        return "os";
    }

    @Override
    public Module[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Exit", new ExitFunction());
        symbols.put("Time", new TimeFunction());
        symbols.put("Exec", new ExecFunction());

        symbols.put("LineSeparator", new StringValue(System.getProperty("line.separator")));
        symbols.put("FileSeparator", new StringValue(System.getProperty("file.separator")));
        symbols.put("PathSeparator", new StringValue(System.getProperty("path.separator")));

        symbols.put("UserDir", new StringValue(System.getProperty("user.dir")));
        symbols.put("UserHome", new StringValue(System.getProperty("user.home")));
        symbols.put("UserName", new StringValue(System.getProperty("user.name")));

        symbols.put("Name", new StringValue(System.getProperty("os.name")));
        symbols.put("Version", new StringValue(System.getProperty("os.version")));
        symbols.put("Arch", new StringValue(System.getProperty("os.arch")));

        symbols.put("Env", getEnvDict());

        symbols.put("Args", args);
    }

    private DictValue getEnvDict() {
        DictValue value = new DictValue();

        System.getenv().entrySet().stream().forEach((entry) -> {
            value.setEntry(new StringValue(entry.getKey()), new StringValue(entry.getValue()));
        });

        return value;
    }
}
