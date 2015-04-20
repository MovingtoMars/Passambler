package passambler.value.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Block;
import passambler.exception.EngineException;
import passambler.value.Property;
import passambler.value.Value;
import passambler.value.ClassValue;

public class ClassInitializerFunction extends UserFunction {
    private String name;

    private List<ClassInitializerFunction> parents = new ArrayList<>();

    public ClassInitializerFunction(String name, Block block, List<ArgumentDefinition> argumentDefinitions) {
        super(block, argumentDefinitions);

        this.name = name;
    }

    public void addParent(ClassInitializerFunction parent) throws EngineException {
        parents.add(parent);
    }

    public List<UserFunction> getFunctions() {
        List<UserFunction> functions = new ArrayList<>();

        for (Map.Entry<String, Property> property : properties.entrySet()) {
            Value propertyValue = property.getValue().getValue();

            if (propertyValue instanceof UserFunction) {
                functions.add((UserFunction) propertyValue);
            }
        }

        return functions;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        getBlock().refreshParser();

        ClassValue child = new ClassValue(name);

        Value self = new Value();

        // Constructor arguments
        for (int i = 0; i < getArguments(); ++i) {
            self.setProperty(getArgumentDefinitions().get(i).getName(), context.getArgument(i));
        }

        // Apply symbols from parents
        for (ClassInitializerFunction parent : parents) {
            for (Map.Entry<String, Value> symbol : parent.getBlock().getParser().getScope().getSymbols().entrySet()) {
                self.setProperty(symbol.getKey(), symbol.getValue());

                if (Lexer.isPublic(symbol.getKey())) {
                    child.setProperty(symbol.getKey(), symbol.getValue());
                }
            }
        }

        getBlock().getParser().getScope().setSymbol("self", self);

        getBlock().invoke();

        // Apply all the symbols to the class
        for (Map.Entry<String, Value> symbol : getBlock().getParser().getScope().getSymbols().entrySet()) {
            if (Lexer.isPublic(symbol.getKey())) {
                child.setProperty(symbol.getKey(), symbol.getValue());
            }

            self.setProperty(symbol.getKey(), symbol.getValue());
        }

        // Invoke constructor
        if (child.hasProperty(name) && child.getProperty(name).getValue() instanceof UserFunction) {
            ((UserFunction) child.getProperty(name).getValue()).invoke(context);
        }

        return child;
    }

    @Override
    public String toString() {
        return name;
    }
}
