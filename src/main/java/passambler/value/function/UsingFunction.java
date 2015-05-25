package passambler.value.function;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import passambler.value.Value;
import passambler.value.StringValue;
import passambler.module.Module;
import passambler.module.FilesystemModule;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.ListValue;

public class UsingFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        ListValue array = new ListValue();

        for (Value value : context.getArguments()) {
            Module currentModule = null;
            String currentModuleName = null;

            String valueName = null;

            for (String child : ((StringValue) value).toString().split("/")) {
                if (child.contains(".")) {
                    valueName = child.split(Pattern.quote("."))[1];

                    currentModuleName = child.split(Pattern.quote("."))[0];
                } else {
                    currentModuleName = child;
                }

                if (currentModule == null) {
                    currentModule = getModule(context.getParser().getModules(), currentModuleName);
                } else {
                    boolean found = false;

                    for (Module childModule : currentModule.getChildren()) {
                        if (childModule.getId().equals(currentModuleName)) {
                            currentModule = childModule;

                            found = true;

                            break;
                        }
                    }

                    if (!found) {
                        throw new ErrorException("Undefined module '%s'", currentModuleName);
                    }
                }
            }

            if (currentModule != null) {
                Map<String, Value> symbols = new HashMap();

                currentModule.apply(symbols);

                if (valueName == null) {
                    Value moduleValue = new Value();

                    for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                        moduleValue.setProperty(symbol.getKey(), symbol.getValue());
                    }

                    if (!context.isAssignment()) {
                        context.getParser().getScope().setSymbol(currentModuleName, moduleValue);
                    }

                    array.getValue().add(moduleValue);
                } else {
                    boolean found = false;

                    for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                        if (symbol.getKey().equals(valueName)) {
                            if (!context.isAssignment()) {
                                context.getParser().getScope().setSymbol(symbol.getKey(), symbol.getValue());
                            }

                            array.getValue().add(symbol.getValue());

                            found = true;
                        }
                    }

                    if (!found) {
                        throw new ErrorException("Undefined module property '%s'", valueName);
                    }
                }
            }
        }

        return array.getValue().size() == 1 ? array.getValue().get(0) : array;
    }

    private Module getModule(List<Module> modules, String name) {
        Module module = modules.stream().filter(p -> p.getId().equals(name)).findFirst().orElse(null);

        if (module == null) {
            return new FilesystemModule(Paths.get(name));
        }

        return module;
    }
}
