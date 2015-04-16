package passambler.bundle.file.function;

import java.nio.file.Paths;
import passambler.exception.EngineException;
import passambler.bundle.file.value.FileValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;

public class OpenFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return new FileValue(Paths.get(((StringValue) context.getArgument(0)).getValue()));
    }
}
