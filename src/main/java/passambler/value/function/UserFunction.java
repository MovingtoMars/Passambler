package passambler.value.function;

import java.util.List;
import passambler.parser.Argument;
import passambler.parser.Block;
import passambler.exception.EngineException;
import passambler.value.Value;

public class UserFunction extends Value implements Function {
    private Block block;
    private List<Argument> arguments;

    public UserFunction(Block block, List<Argument> arguments) {
        this.block = block;
        this.arguments = arguments;
    }

    public Block getBlock() {
        return block;
    }

    public List<Argument> getArgumentDefinitions() {
        return arguments;
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

            return block.invoke();
        }

        return null;
    }
}
