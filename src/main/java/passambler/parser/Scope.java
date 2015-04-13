package passambler.parser;

import java.util.HashMap;
import java.util.Map;
import passambler.value.function.FunctionUsing;
import passambler.value.function.FunctionThrow;
import passambler.lexer.Lexer;
import passambler.value.Value;

public class Scope {
    private Scope parent;

    private Map<String, Value> symbols = new HashMap();

    public Scope() {
        this(null);
    }

    public Scope(Scope parentScope) {
        parent = parentScope;

        symbols.put("using", new FunctionUsing());
        symbols.put("throw", new FunctionThrow());
    }

    public void setSymbol(String key, Value value) {
        if (parent != null && parent.hasSymbol(key)) {
            parent.setSymbol(key, value);
        } else {
            if (symbols.containsKey(key) && symbols.get(key).isConstant()) {
                throw new RuntimeException(String.format("Cannot modify constant '%s'", key));
            }

            if (Lexer.isConstant(key)) {
                value.setConstant(true);
            }

            symbols.put(key, value);
        }
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
