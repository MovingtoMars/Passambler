package passambler.parser;

import java.util.HashMap;
import java.util.Map;
import passambler.function.FunctionExit;
import passambler.function.FunctionSqrt;
import passambler.function.Function;
import passambler.function.FunctionRandom;
import passambler.function.FunctionWrite;
import passambler.value.Value;
import passambler.value.ValueBlock;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

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
        setSymbol("exit", new FunctionExit());
        setSymbol("sqrt", new FunctionSqrt());
        setSymbol("random", new FunctionRandom());
        setSymbol("write", new FunctionWrite(false));
        setSymbol("writeln", new FunctionWrite(true));
        setSymbol("nil", Value.nil.lock());
        setSymbol("pi", new ValueNum(Math.PI).lock());
        setSymbol("max_num", new ValueNum(Integer.MAX_VALUE).lock());
        setSymbol("min_num", new ValueNum(Integer.MIN_VALUE).lock());
        setSymbol("true", new ValueBool(true).lock());
        setSymbol("false", new ValueBool(false).lock());
    }

    public void setSymbol(String key, Value value) {
        if (parent != null && parent.hasSymbol(key)) {
            parent.setSymbol(key, value);
        } else if (symbols.containsKey(key) && symbols.get(key).isLocked()) {
            throw new RuntimeException(String.format("Variable %s is locked", key));
        } else {
            symbols.put(key, value);
        }
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
}
