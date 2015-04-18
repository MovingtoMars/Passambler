package passambler.value.function;

import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.ReadableValue;

public class ReadFunction extends Value implements Function {
    private boolean line;

    public ReadFunction(boolean line) {
        this.line = line;
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof ReadableValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        return ((ReadableValue) context.getArgument(0)).read(line);
    }
}
