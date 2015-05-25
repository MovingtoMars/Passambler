package passambler.module.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.NumberValue;

public class SizeFunction extends SimpleFileFunction {
    @Override
    public Value getReturnValue(Path file) throws EngineException {
        try {
            return new NumberValue(Files.size(file));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
