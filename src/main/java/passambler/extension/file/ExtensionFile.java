package passambler.extension.file;

import java.util.Map;
import passambler.extension.file.procedure.*;
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
        symbols.put("create", new ProcedureCreate());
        symbols.put("exists", new ProcedureExists());
        symbols.put("remove", new ProcedureRemove());
        symbols.put("write", new ProcedureWrite());
        symbols.put("read", new ProcedureRead());
        symbols.put("size", new ProcedureSize());
        symbols.put("copy", new ProcedureCopy());
        symbols.put("move", new ProcedureMove());
        symbols.put("isdir", new ProcedureIsDir());
        symbols.put("createdir", new ProcedureCreateDir());
    }
}