package passambler.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Block;
import passambler.parser.ParserException;
import passambler.value.function.FunctionContext;
import passambler.value.function.FunctionUser;

public class ValueClass extends FunctionUser {
    private String name;

    private Value self = new Value();

    public ValueClass(String name, Block block, List<ArgumentDefinition> argumentDefinitions) {
        super(block, argumentDefinitions);

        this.name = name;
    }

    public void applyBlockSymbols(Block block) throws ParserException {
        block.invoke();

        for (Map.Entry<String, Value> symbol : block.getParser().getScope().getSymbols().entrySet()) {
            setProperty(symbol.getKey(), symbol.getValue());
        }
    }

    @Override
    public void setProperty(String key, Value value) {
        if (value instanceof FunctionUser && ((FunctionUser) value).getBlock() != null) {
            ((FunctionUser) value).getBlock().getParser().getScope().setSymbol("self", self);
        }

        self.setProperty(key, value);

        if (Lexer.isPublic(key)) {
            super.setProperty(key, value);
        }
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
        ValueClass classValue = new ValueClass(name, getBlock(), getArgumentDefinitions());

        classValue.applyBlockSymbols(getBlock());

        if (classValue.getFunctions().stream().anyMatch(f -> f.getBlock() == null)) {
            throw new ParserException(ParserException.Type.CANNOT_INSTANTIATE_EMPTY_FUNCTIONS, name);
        }

        for (int i = 0; i < getArguments(); ++i) {
            classValue.setProperty(getArgumentDefinitions().get(i).getName(), context.getArgument(i));
        }

        // Invoke constructor
        if (hasProperty(name) && getProperty(name).getValue() instanceof FunctionUser) {
            ((FunctionUser) classValue.getProperty(name).getValue()).invoke(context);
        }

        return classValue;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
