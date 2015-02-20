package passambler.extension.str;

import passambler.extension.Extension;
import passambler.extension.str.procedure.*;
import passambler.parser.Scope;

public class ExtensionStr implements Extension {
    @Override
    public String getId() {
        return "str";
    }
    
    @Override
    public void applySymbols(Scope scope) {
        scope.setSymbol("str", new ProcedureStr());
        scope.setSymbol("lcase", new ProcedureLCase());
        scope.setSymbol("ucase", new ProcedureUCase());
        scope.setSymbol("contains", new ProcedureContains());
        scope.setSymbol("indexof", new ProcedureIndexOf());
        scope.setSymbol("replace", new ProcedureReplace());
        scope.setSymbol("split", new ProcedureSplit());
    }
}
