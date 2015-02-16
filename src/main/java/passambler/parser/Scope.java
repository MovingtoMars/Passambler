package passambler.parser;

import java.util.HashMap;
import java.util.Map;
import passambler.function.Function;
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
        setSymbol("exit", Function.FUNCTION_EXIT);
        setSymbol("sqrt", Function.FUNCTION_SQRT);
        setSymbol("random", Function.FUNCTION_RANDOM);
        
        setSymbol("write", Function.FUNCTION_WRITE);
        setSymbol("writeln", Function.FUNCTION_WRITELN);
        
        setSymbol("readstr", Function.FUNCTION_READSTR);
        setSymbol("readdouble", Function.FUNCTION_READDOUBLE);
        setSymbol("readint", Function.FUNCTION_READINT);
        
        setSymbol("sin", Function.FUNCTION_SIN);
        setSymbol("cos", Function.FUNCTION_COS);
        setSymbol("tan", Function.FUNCTION_TAN);
        
        setSymbol("log", Function.FUNCTION_LOG);
        setSymbol("log10", Function.FUNCTION_LOG10);
        
        setSymbol("min", Function.FUNCTION_MIN);
        setSymbol("max", Function.FUNCTION_MAX);
        
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

    public void setSymbol(String key, Function function) {
        symbols.put(key, ValueBlock.transform(function));
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
