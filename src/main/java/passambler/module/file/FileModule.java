package passambler.module.file;

import java.util.Map;
import passambler.module.Module;
import passambler.module.file.function.*;
import passambler.value.Value;

public class FileModule implements Module {
    @Override
    public String getId() {
        return "file";
    }

    @Override
    public Module[] getChildren() {
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
        symbols.put("Files", new FilesFunction());
    }
}
