package passambler.pack.file;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.file.function.*;
import passambler.value.Value;

public class PackageFile implements Package {
    @Override
    public void addSymbols(Map<String, Value> symbols) {
        symbols.put("Create", new FunctionCreate());
        symbols.put("Exists", new FunctionExists());
        symbols.put("Delete", new FunctionDelete());
        symbols.put("Write", new FunctionWrite());
        symbols.put("Read", new FunctionRead());
        symbols.put("Size", new FunctionSize());
        symbols.put("Copy", new FunctionCopy());
        symbols.put("Move", new FunctionMove());
        symbols.put("IsDir", new FunctionIsDir());
        symbols.put("CreateDir", new FunctionCreateDir());
        symbols.put("Type", new FunctionType());
        symbols.put("Modified", new FunctionModified());
        symbols.put("Touch", new FunctionTouch());
    }
}
