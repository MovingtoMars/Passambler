package passambler.function;

import java.util.List;
import passambler.parser.Block;
import passambler.parser.ParserException;
import passambler.value.Value;

public class FunctionUser extends Function {
    private Block callback;
    private List<String> argumentNames;

    public FunctionUser(Block callback, List<String> argumentNames) {
        this.callback = callback;
        this.argumentNames = argumentNames;
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public int getArguments() {
        return argumentNames.size();
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return argument < argumentNames.size();
    }

    @Override
    public Value invoke(FunctionContext context) throws ParserException {
        for (int i = 0; i < getArgumentNames().size(); ++i) {
            callback.getParser().getScope().setSymbol(argumentNames.get(i), context.getArgument(i));
        }

        return callback.invoke();
    }
}
