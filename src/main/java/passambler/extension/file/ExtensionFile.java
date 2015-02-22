package passambler.extension.file;

import java.util.Map;
import passambler.extension.file.function.*;
import passambler.extension.Extension;
import passambler.parser.Scope;
import passambler.value.Value;

public class ExtensionFile implements Extension {
    @Override
    public String getId() {
        return "file";
    }

    @Override
    public void addSymbols(Scope scope, Map<String, Value> symbols) {
        symbols.put("create", new FunctionCreate());
        symbols.put("exists", new FunctionExists());
        symbols.put("remove", new FunctionRemove());
        symbols.put("write", new FunctionWrite());
        symbols.put("read", new FunctionRead());
        symbols.put("size", new FunctionSize());
        symbols.put("copy", new FunctionCopy());
        symbols.put("move", new FunctionMove());
        symbols.put("isdir", new FunctionIsDir());
        symbols.put("createdir", new FunctionCreateDir());
    }
}