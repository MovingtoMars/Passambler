package passambler.pack.file;

import java.util.Map;
import passambler.pack.Package;
import passambler.pack.file.function.*;
import passambler.value.Value;

public class FilePackage implements Package {
    @Override
    public String getId() {
        return "file";
    }

    @Override
    public Package[] getChildren() {
        return null;
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        symbols.put("Open", new OpenFunction());
        symbols.put("Create", new CreateFunction());
        symbols.put("Exists", new ExistsFunction());
        symbols.put("Delete", new DeleteFunction());
        symbols.put("Size", new SizeFunction());
        symbols.put("Copy", new CopyFunction());
        symbols.put("Move", new MoveFunction());
        symbols.put("IsDir", new IsDirFunction());
        symbols.put("CreateDir", new CreateDirFunction());
        symbols.put("Type", new TypeFunction());
        symbols.put("Modified", new ModifiedFunction());
        symbols.put("Touch", new TouchFunction());
    }
}
