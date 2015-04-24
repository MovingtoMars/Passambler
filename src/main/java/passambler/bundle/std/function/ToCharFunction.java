package passambler.bundle.std.function;

import passambler.exception.EngineException;
import passambler.value.CharacterValue;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;

public class ToCharFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return true;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new CharacterValue(context.getArgument(0).toString().charAt(0));
    }
}
