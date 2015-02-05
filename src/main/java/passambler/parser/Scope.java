package passambler.parser;

import java.util.HashMap;
import java.util.Map;
import passambler.function.FunctionArray;
import passambler.function.FunctionExit;
import passambler.function.FunctionList;
import passambler.function.FunctionSqrt;
import passambler.function.Function;
import passambler.function.FunctionStr;
import passambler.function.FunctionRand;
import passambler.val.Val;
import passambler.val.ValBool;
import passambler.val.ValInputStream;
import passambler.val.ValNumber;
import passambler.val.ValPrintStream;

public class Scope {
    private Scope parent;

    private Map<String, Val> variables = new HashMap();

    private Map<String, Function> functions = new HashMap();

    public Scope() {
        this(null);
    }

    public Scope(Scope parentScope) {
        parent = parentScope;

        functions.put("exit", new FunctionExit());
        functions.put("array", new FunctionArray());
        functions.put("list", new FunctionList());
        functions.put("sqrt", new FunctionSqrt());
        functions.put("rand", new FunctionRand());
        functions.put("str", new FunctionStr());

        variables.put("nil", Val.nil.lock());
        variables.put("pi", new ValNumber(Math.PI).lock());
        variables.put("stdout", new ValPrintStream(System.out).lock());
        variables.put("stderr", new ValPrintStream(System.err).lock());
        variables.put("stdin", new ValInputStream(System.in).lock());
        variables.put("true", new ValBool(true).lock());
        variables.put("false", new ValBool(false).lock());
    }

    public void setVariable(String key, Val value) {
        if (parent != null && parent.hasVariable(key)) {
            parent.setVariable(key, value);
        } else if (variables.containsKey(key) && variables.get(key).isLocked()) {
            throw new RuntimeException(String.format("Variable %s is locked", key));
        } else {
            variables.put(key, value);
        }
    }

    public Val getVariable(String key) {
        if (variables.containsKey(key)) {
            return variables.get(key);
        } else if (parent != null) {
            return parent.getVariable(key);
        } else {
            return null;
        }
    }

    public boolean hasVariable(String key) {
        return getVariable(key) != null;
    }

    public void setFunction(String key, Function value) {
        functions.put(key, value);
    }

    public Function getFunction(String key) {
        if (functions.containsKey(key)) {
            return functions.get(key);
        } else if (parent != null) {
            return parent.getFunction(key);
        } else {
            return null;
        }
    }

    public boolean hasFunction(String key) {
        return getFunction(key) != null;
    }
}
