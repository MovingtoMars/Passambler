package passambler.value.function;

import java.util.List;
import passambler.parser.ArgumentDefinition;
import passambler.parser.Block;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.parser.typehint.AnyTypehint;
import passambler.parser.typehint.Typehint;
import passambler.value.ErrorValue;
import passambler.value.Value;

public class UserFunction extends Value implements Function {
    private Block block;
    private List<ArgumentDefinition> arguments;
    private Typehint typehint = new AnyTypehint();

    public UserFunction(Block block, List<ArgumentDefinition> arguments) {
        this.block = block;
        this.arguments = arguments;
    }

    public Block getBlock() {
        return block;
    }

    public List<ArgumentDefinition> getArgumentDefinitions() {
        return arguments;
    }

    public void setTypehint(Typehint typehint) {
        this.typehint = typehint;
    }

    @Override
    public int getArguments() {
        return arguments.size();
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return argument < getArguments();
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        if (block != null) {
            for (int i = 0; i < getArguments(); ++i) {
                block.getParser().getScope().setSymbol(arguments.get(i).getName(), context.getArgument(i));
            }

            Value result = block.invoke();

            if (!typehint.matches(result)) {
                throw new ErrorException(new ErrorValue(String.format("Type mismatch")));
            }

            return result;
        }

        return null;
    }
}
