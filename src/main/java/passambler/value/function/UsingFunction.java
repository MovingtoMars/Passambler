package passambler.value.function;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import passambler.value.Value;
import passambler.value.StringValue;
import passambler.bundle.Bundle;
import passambler.bundle.FilesystemBundle;
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
            Bundle currentBundle = null;
            String currentBundleName = null;

            String valueName = null;

            for (String child : ((StringValue) value).toString().split("/")) {
                if (child.contains(".")) {
                    valueName = child.split(Pattern.quote("."))[1];

                    currentBundleName = child.split(Pattern.quote("."))[0];
                } else {
                    currentBundleName = child;
                }

                if (currentBundle == null) {
                    currentBundle = getBundle(context.getParser().getBundles(), currentBundleName);
                } else {
                    boolean found = false;

                    for (Bundle childBundle : currentBundle.getChildren()) {
                        if (childBundle.getId().equals(currentBundleName)) {
                            currentBundle = childBundle;

                            found = true;

                            break;
                        }
                    }

                    if (!found) {
                        throw new ErrorException(new ErrorValue("Undefined bundle: '%s'", currentBundleName));
                    }
                }
            }

            if (currentBundle != null) {
                Map<String, Value> symbols = new HashMap();

                currentBundle.apply(symbols);

                if (valueName == null) {
                    Value bundleValue = new Value();

                    for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                        bundleValue.setProperty(symbol.getKey(), symbol.getValue());
                    }

                    if (!context.isAssignment()) {
                        context.getParser().getScope().setSymbol(currentBundleName, bundleValue);
                    }

                    array.getValue().add(bundleValue);
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
                        throw new ErrorException(new ErrorValue("Undefined bundle property: '%s'", valueName));
                    }
                }
            }
        }

        return array.getValue().size() == 1 ? array.getValue().get(0) : array;
    }

    private Bundle getBundle(List<Bundle> bundles, String name) {
        Bundle bundle = bundles.stream().filter(p -> p.getId().equals(name)).findFirst().orElse(null);

        if (bundle == null) {
            return new FilesystemBundle(Paths.get(name));
        }

        return bundle;
    }
}
