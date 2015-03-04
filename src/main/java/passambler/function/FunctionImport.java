package passambler.function;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import passambler.parser.ParserException;
import passambler.value.Value;
import passambler.value.ValueStr;
import passambler.pack.Package;
import passambler.pack.PackageFileSystem;

public class FunctionImport extends Function {
    @Override
    public int getArguments() {
        return -1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ValueStr;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        if (context.isAssignment() && context.getArguments().length > 1) {
            throw new ParserException(ParserException.Type.BAD_SYNTAX, null, "can only import 1 value when assigning");
        }

        for (int i = 0; i < context.getArguments().length; ++i) {
            String specificValue = null;

            String currentPackageName = null;

            Package currentPackage = null;

            String[] children = ((ValueStr) context.getArgument(i)).getValue().split("/");

            for (int x = 0; x < children.length; ++x) {
                String rawChild = children[x];

                if (rawChild.contains(".")) {
                    specificValue = rawChild.substring(rawChild.indexOf('.') + 1);
                    rawChild = rawChild.substring(0, rawChild.indexOf('.'));
                }

                final String child = rawChild;

                if (currentPackage == null) {
                    if (context.getParser().getDefaultPackages().stream().anyMatch(p -> p.getId().equals(child))) {
                        currentPackage = context.getParser().getDefaultPackages().stream()
                            .filter(p -> p.getId().equals(child))
                            .findFirst()
                            .get();
                    } else {
                        currentPackage = new PackageFileSystem(Paths.get(child));
                    }
                } else {
                    currentPackage = Arrays.asList(currentPackage.getChildren()).stream()
                        .filter(p -> p.getId().equals(child))
                        .findFirst()
                        .orElseThrow(() -> new ParserException(ParserException.Type.UNDEFINED_PACKAGE, null, child));
                }

                currentPackageName = child;
            }

            if (currentPackage != null) {
                Map<String, Value> symbols = new HashMap();

                currentPackage.apply(symbols);

                if (specificValue != null) {
                    final String key = specificValue;

                    if (key.equals("*")) {
                        if (context.isAssignment()) {
                            throw new ParserException(ParserException.Type.BAD_SYNTAX, null, "can't import with * when assigning");
                        }

                        context.getParser().getScope().getSymbols().putAll(symbols);
                    } else {
                        Value value = symbols.entrySet().stream()
                            .filter(s -> s.getKey().equals(key))
                            .findFirst()
                            .orElseThrow(() -> new ParserException(ParserException.Type.UNDEFINED_PROPERTY, null, key))
                            .getValue();

                        if (context.isAssignment()) {
                            return value;
                        } else {
                            context.getParser().getScope().setSymbol(key, value);
                        }
                    }
                } else {
                    Value value = new Value();

                    for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                        value.setProperty(symbol.getKey(), symbol.getValue());
                    }

                    if (context.isAssignment()) {
                        return value;
                    } else {
                        context.getParser().getScope().setSymbol(currentPackageName, value);
                    }
                }
            }
        }

        return null;
    }
}
