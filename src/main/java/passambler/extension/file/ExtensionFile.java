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
        symbols.put("fcreate", new ProcedureFCreate());
        symbols.put("fexists", new ProcedureFExists());
        symbols.put("fremove", new ProcedureFRemove());
        symbols.put("fwrite", new ProcedureFWrite());
        symbols.put("fread", new ProcedureFRead());
        symbols.put("fsize", new ProcedureFSize());
        symbols.put("fcopy", new ProcedureFCopy());
        symbols.put("fmove", new ProcedureFMove());
        symbols.put("isdir", new ProcedureIsDir());
        symbols.put("createdir", new ProcedureCreateDir());
    }
}