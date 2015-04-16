package passambler.pack.os;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.os.function.*;
import passambler.value.Value;
import passambler.value.StringValue;

public class OsPackage implements Package {
    @Override
    public String getId() {
        return "os";
    }

    @Override
    public Package[] getChildren() {
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
    }
}
