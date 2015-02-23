package passambler.pkg.file;

import java.util.Map;
import passambler.pkg.Package;
import passambler.parser.Scope;
import passambler.pkg.file.function.*;
import passambler.value.Value;

public class PackageFile implements Package {
    @Override
    public String getId() {
        return "file";
    }

    @Override
    public void addSymbols(Scope scope, Map<String, Value> symbols) {
        symbols.put("create", new FunctionCreate());
        symbols.put("exists", new FunctionExists());
        symbols.put("delete", new FunctionDelete());
        symbols.put("write", new FunctionWrite());
        symbols.put("read", new FunctionRead());
        symbols.put("size", new FunctionSize());
        symbols.put("copy", new FunctionCopy());
        symbols.put("move", new FunctionMove());
        symbols.put("isdir", new FunctionIsDir());
        symbols.put("createdir", new FunctionCreateDir());
        symbols.put("type", new FunctionType());
        symbols.put("modified", new FunctionModified());
    }
}
