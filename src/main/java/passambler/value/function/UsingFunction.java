package passambler.value.function;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import passambler.value.Value;
import passambler.value.StringValue;
import passambler.pack.Package;
import passambler.pack.FileSystemPackage;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.ErrorValue;
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
            Package currentPackage = null;
            String currentPackageName = null;

            String valueName = null;

            for (String child : ((StringValue) value).getValue().split("/")) {
                if (child.contains(".")) {
                    valueName = child.split(Pattern.quote("."))[1];

                    currentPackageName = child.split(Pattern.quote("."))[0];
                } else {
                    currentPackageName = child;
                }

                if (currentPackage == null) {
                    currentPackage = getPackage(context.getParser().getPackages(), currentPackageName);
                } else {
                    boolean found = false;

                    for (Package childPackage : currentPackage.getChildren()) {
                        if (childPackage.getId().equals(currentPackageName)) {
                            currentPackage = childPackage;

                            found = true;

                            break;
                        }
                    }

                    if (!found) {
                        throw new ErrorException(new ErrorValue(String.format("Undefined package: '%s'", currentPackageName)));
                    }
                }
            }

            if (currentPackage != null) {
                Map<String, Value> symbols = new HashMap();

                currentPackage.apply(symbols);

                if (valueName == null) {
                    Value packageValue = new Value();

                    for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                        packageValue.setProperty(symbol.getKey(), symbol.getValue());
                    }

                    if (!context.isAssignment()) {
                        context.getParser().getScope().setSymbol(currentPackageName, packageValue);
                    }

                    array.getValue().add(packageValue);
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
                        throw new ErrorException(new ErrorValue(String.format("Undefined package property: '%s'", valueName)));
                    }
                }
            }
        }

        return array.getValue().size() == 1 ? array.getValue().get(0) : array;
    }

    private Package getPackage(List<Package> defaultPackages, String name) {
        Package defaultPackage = defaultPackages.stream().filter(p -> p.getId().equals(name)).findFirst().orElse(null);

        if (defaultPackage == null) {
            return new FileSystemPackage(Paths.get(name));
        }

        return defaultPackage;
    }
}
