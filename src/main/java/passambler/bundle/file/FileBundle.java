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
        symbols.put("open", new OpenFunction());
        symbols.put("create", new CreateFunction());
        symbols.put("exists", new ExistsFunction());
        symbols.put("delete", new DeleteFunction());
        symbols.put("size", new SizeFunction());
        symbols.put("copy", new CopyFunction());
        symbols.put("move", new MoveFunction());
        symbols.put("is_dir", new IsDirFunction());
        symbols.put("create_dir", new CreateDirFunction());
        symbols.put("type", new TypeFunction());
        symbols.put("modified", new ModifiedFunction());
        symbols.put("touch", new TouchFunction());
    }
}
