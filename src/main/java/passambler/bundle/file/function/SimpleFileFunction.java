package passambler.bundle.file.function;

import java.nio.file.Path;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.exception.EngineException;
import passambler.bundle.file.value.FileValue;
import passambler.value.Value;

public abstract class SimpleFileFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof FileValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return getReturnValue(((FileValue) context.getArgument(0)).getValue());
    }

    public abstract Value getReturnValue(Path file) throws EngineException;
}
