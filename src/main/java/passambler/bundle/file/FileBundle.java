package passambler.bundle.file;

import java.util.Map;
import passambler.bundle.Bundle;
import passambler.bundle.file.function.*;
import passambler.value.Value;

public class FileBundle implements Bundle {
    @Override
    public String getId() {
        return "file";
    }

    @Override
    public Bundle[] getChildren() {
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
