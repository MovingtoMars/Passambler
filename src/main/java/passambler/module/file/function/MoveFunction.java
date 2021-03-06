package passambler.module.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.function.Function;
import passambler.value.function.FunctionContext;
import passambler.value.Value;
import passambler.value.StringValue;

public class MoveFunction extends Value implements Function {
    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public boolean isArgumentValid(Value value, int argument) {
        return value instanceof StringValue;
    }

    @Override
    public Value invoke(FunctionContext context) throws EngineException {
        try {
            Path file = Paths.get(((StringValue) context.getArgument(0)).toString());
            Path destination = Paths.get(((StringValue) context.getArgument(1)).toString());

            Files.move(file, destination);
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
