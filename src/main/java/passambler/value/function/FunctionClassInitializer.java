package passambler.value.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Block;
import passambler.parser.ParserException;
import passambler.value.Property;
import passambler.value.Value;
import passambler.value.ValueClass;

public class FunctionClassInitializer extends FunctionUser {
    private String name;

    private List<FunctionClassInitializer> parents = new ArrayList<>();

    public FunctionClassInitializer(String name, Block block, List<ArgumentDefinition> argumentDefinitions) {
        super(block, argumentDefinitions);

        this.name = name;
    }

    public void addParent(FunctionClassInitializer parent) throws ParserException {
        parents.add(parent);
    }

    public List<FunctionUser> getFunctions() {
        List<FunctionUser> functions = new ArrayList<>();

        for (Map.Entry<String, Property> property : properties.entrySet()) {
            Value propertyValue = property.getValue().getValue();

            if (propertyValue instanceof FunctionUser) {
                functions.add((FunctionUser) propertyValue);
            }
        }

        return functions;
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        getBlock().refreshParser();

        ValueClass child = new ValueClass(name);

        Value self = new Value();

        // Constructor arguments
        for (int i = 0; i < getArguments(); ++i) {
            self.setProperty(getArgumentDefinitions().get(i).getName(), context.getArgument(i));
        }

        // Apply symbols from parents
        for (FunctionClassInitializer parent : parents) {
            for (Map.Entry<String, Value> symbol : parent.getBlock().getParser().getScope().getSymbols().entrySet()) {
                self.setProperty(symbol.getKey(), symbol.getValue());
            }
        }

        getBlock().getParser().getScope().setSymbol("self", self);

        getBlock().invoke();

        // Apply all the symbols to the class
        for (Map.Entry<String, Value> symbol : getBlock().getParser().getScope().getSymbols().entrySet()) {
            if (Lexer.isPublic(symbol.getKey())) {
                child.setProperty(symbol.getKey(), symbol.getValue());
            }
        }

        // Invoke constructor
        if (child.hasProperty(name) && child.getProperty(name).getValue() instanceof FunctionUser) {
            ((FunctionUser) child.getProperty(name).getValue()).invoke(context);
        }

        return child;
    }

    @Override
    public String toString() {
        return name;
    }
}
