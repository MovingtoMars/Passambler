package passambler.extension.file;

import passambler.extension.file.procedure.ProcedureIsDir;
import passambler.extension.file.procedure.ProcedureFSize;
import passambler.extension.file.procedure.ProcedureFRead;
import passambler.extension.file.procedure.ProcedureCreateDir;
import passambler.extension.file.procedure.ProcedureFWrite;
import passambler.extension.file.procedure.ProcedureFExists;
import passambler.extension.file.procedure.ProcedureFRemove;
import passambler.extension.file.procedure.ProcedureFCreate;
import passambler.extension.Extension;
import passambler.parser.Scope;

public class ExtensionFile implements Extension {
    @Override
    public String getId() {
        return "file";
    }

    @Override
    public void applySymbols(Scope scope) {
        scope.setSymbol("fcreate", new ProcedureFCreate());
        scope.setSymbol("fexists", new ProcedureFExists());
        scope.setSymbol("fremove", new ProcedureFRemove());
        scope.setSymbol("fwrite", new ProcedureFWrite());
        scope.setSymbol("fread", new ProcedureFRead());
        scope.setSymbol("fsize", new ProcedureFSize());
        scope.setSymbol("isdir", new ProcedureIsDir());
        scope.setSymbol("createdir", new ProcedureCreateDir());
    }
}