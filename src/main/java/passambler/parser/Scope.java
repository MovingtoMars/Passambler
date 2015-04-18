package passambler.parser;

import java.util.HashMap;
import java.util.Map;
import passambler.bundle.std.value.OutValue;
import passambler.value.function.UsingFunction;
import passambler.value.function.ThrowFunction;
import passambler.value.function.ReadFunction;
import passambler.value.function.WriteFunction;
import passambler.value.function.CloseFunction;
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

        symbols.put("using", new UsingFunction());
        symbols.put("throw", new ThrowFunction());
        symbols.put("write", new WriteFunction(new OutValue(), false));
        symbols.put("writeln", new WriteFunction(new OutValue(), true));
        symbols.put("read", new ReadFunction(false));
        symbols.put("readln", new ReadFunction(true));
        symbols.put("close", new CloseFunction());
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
