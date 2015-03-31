package passambler.value;

import java.util.Map;
import passambler.lexer.Lexer;
import passambler.parser.Block;
import passambler.value.function.FunctionUser;

public class ValueClass extends Value {
    private Value thisValue = new Value();

    private Block block;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    @Override
    public void setProperty(String key, Value value) {
        if (value instanceof FunctionUser && ((FunctionUser) value).getBlock() != null) {
            ((FunctionUser) value).getBlock().getParser().getScope().setSymbol("this", thisValue);
        }

        thisValue.setProperty(key, value);

        if (Lexer.isPublic(key)) {
            super.setProperty(key, value);
        }
    }

    public boolean hasEmptyFunctions() {
        for (Map.Entry<String, Property> property : properties.entrySet()) {
            Value propertyValue = property.getValue().getValue();

            if (propertyValue instanceof FunctionUser && ((FunctionUser) propertyValue).getBlock() == null) {
                return true;
            }
        }

        return false;
    }
}
