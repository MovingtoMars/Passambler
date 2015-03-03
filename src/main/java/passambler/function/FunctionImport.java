package passambler.function;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import passambler.parser.Parser;
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
    public Value invoke(Parser parser, Value... arguments) throws ParserException {
        for (int i = 0; i < arguments.length; ++i) {
            String specificValue = null;

            String currentPackageName = null;

            Package currentPackage = null;

            String[] children = ((ValueStr) arguments[i]).getValue().split("/");

            for (int x = 0; x < children.length; ++x) {
                String rawChild = children[x];

                if (rawChild.contains(".")) {
                    specificValue = rawChild.substring(rawChild.indexOf('.') + 1);
                    rawChild = rawChild.substring(0, rawChild.indexOf('.'));
                }

                final String child = rawChild;

                if (currentPackage == null) {
                    if (parser.getDefaultPackages().stream().anyMatch(p -> p.getId().equals(child))) {
                        // Default package
                        currentPackage = parser.getDefaultPackages().stream().filter(p -> p.getId().equals(child)).findFirst().get();
                    } else {
                        // Create filesystem package
                        currentPackage = new PackageFileSystem(Paths.get(child));
                    }
                } else {
                    // Import child package
                    currentPackage = Arrays.asList(currentPackage.getChildren()).stream().filter(p -> p.getId().equals(child)).findFirst().get();
                }

                currentPackageName = child;
            }

            if (currentPackage != null) {
                Map<String, Value> symbols = new HashMap();

                currentPackage.apply(symbols);

                if (specificValue != null) {
                    final String value = specificValue;

                    if (value.equals("*")) {
                        parser.getScope().getSymbols().putAll(symbols);
                    } else {
                        parser.getScope().setSymbol(value, symbols.entrySet().stream().filter(s -> s.getKey().equals(value)).findFirst().get().getValue());
                    }
                } else {
                    Value value = new Value();

                    for (Map.Entry<String, Value> symbol : symbols.entrySet()) {
                        value.setProperty(symbol.getKey(), symbol.getValue());
                    }

                    parser.getScope().setSymbol(currentPackageName, value);
                }
            }
        }

        return null;
    }
}
