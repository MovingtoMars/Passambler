package passambler.parser;

import java.util.HashMap;
import java.util.Map;
import passambler.procedure.Procedure;
import passambler.value.Value;
import passambler.value.ValueBlock;

public class Scope {
    private Scope parent;

    private Map<String, Value> symbols = new HashMap();

    public Scope() {
        this(null);
    }

    public Scope(Scope parentScope) {
        parent = parentScope;
    }
    
    public void addStd() {
        setSymbol("exit", Procedure.PROCEDURE_EXIT);
        setSymbol("sqrt", Procedure.PROCEDURE_SQRT);
        setSymbol("random", Procedure.PROCEDURE_RANDOM);
        
        setSymbol("write", Procedure.PROCEDURE_WRITE);
        setSymbol("writeln", Procedure.PROCEDURE_WRITELN);
        
        setSymbol("readstr", Procedure.PROCEDURE_READSTR);
        setSymbol("readdouble", Procedure.PROCEDURE_READDOUBLE);
        setSymbol("readint", Procedure.PROCEDURE_READINT);
        
        setSymbol("sin", Procedure.PROCEDURE_SIN);
        setSymbol("cos", Procedure.PROCEDURE_COS);
        setSymbol("tan", Procedure.PROCEDURE_TAN);
        
        setSymbol("log", Procedure.PROCEDURE_LOG);
        setSymbol("log10", Procedure.PROCEDURE_LOG10);
        
        setSymbol("nil", Value.VALUE_NIL);
        
        setSymbol("pi", Value.VALUE_PI);
        
        setSymbol("true", Value.VALUE_TRUE);
        setSymbol("false", Value.VALUE_FALSE);
        
        setSymbol("stdout", Value.VALUE_STDOUT);
        setSymbol("stderr", Value.VALUE_STDERR);
        
        setSymbol("stdin", Value.VALUE_STDIN);
    }

    public void setSymbol(String key, Value value) {
        if (parent != null && parent.hasSymbol(key)) {
            parent.setSymbol(key, value);
        } else {
            symbols.put(key, value);
        }
    }
    
    public void setSymbol(String key, ValueBlock block) {
        symbols.put(key, block);
    }

    public void setSymbol(String key, Procedure procedure) {
        symbols.put(key, ValueBlock.transform(procedure));
    }
    
    public Value getSymbol(String key) {
        if (symbols.containsKey(key)) {
            return symbols.get(key);
        } else if (parent != null) {
            return parent.getSymbol(key);
        } else {
            return null;
        }
    }

    public boolean hasSymbol(String key) {
        return getSymbol(key) != null;
    }
    
    public Map<String, Value> getSymbols() {
        return symbols;
    }
}
