package passambler.bundle.net.http.function;

import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.bundle.net.http.value.FileHandlerValue;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.StringValue;

public class FileHandlerFunction extends Value implements Function {
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
        return new FileHandlerValue(((StringValue) context.getArgument(0)).getValue());
    }
}
