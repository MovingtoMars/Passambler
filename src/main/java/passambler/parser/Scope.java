package passambler.parser;

import java.util.HashMap;
import java.util.Map;
import passambler.function.FunctionExit;
import passambler.function.FunctionSqrt;
import passambler.function.Function;
import passambler.function.FunctionRandom;
import passambler.function.FunctionWrite;
import passambler.val.Val;
import passambler.val.ValBlock;
import passambler.val.ValBool;
import passambler.val.ValNum;

public class Scope {
    private Scope parent;

    private Map<String, Val> symbols = new HashMap();

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
        setSymbol("nil", Val.nil.lock());
        setSymbol("pi", new ValNum(Math.PI).lock());
        setSymbol("true", new ValBool(true).lock());
        setSymbol("false", new ValBool(false).lock());
    }

    public void setSymbol(String key, Val value) {
        if (parent != null && parent.hasSymbol(key)) {
            parent.setSymbol(key, value);
        } else if (symbols.containsKey(key) && symbols.get(key).isLocked()) {
            throw new RuntimeException(String.format("Variable %s is locked", key));
        } else {
            symbols.put(key, value);
        }
    }

    public void setSymbol(String key, Function function) {
        symbols.put(key, ValBlock.transform(function));
    }
    
    public Val getSymbol(String key) {
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
