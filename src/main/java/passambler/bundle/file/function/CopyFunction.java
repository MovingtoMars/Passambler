package passambler.bundle.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.bundle.file.value.FileValue;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;

public class CopyFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof FileValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            Path file = ((FileValue) context.getArgument(0)).getValue();
            Path destination = ((FileValue) context.getArgument(1)).getValue();

            Files.copy(file, destination);
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
