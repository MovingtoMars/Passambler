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
