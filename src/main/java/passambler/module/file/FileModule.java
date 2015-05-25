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
