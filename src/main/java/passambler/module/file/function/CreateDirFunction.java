package passambler.module.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.Value;

public class CreateDirFunction extends SimpleFileFunction {
    @Override
    public Value getReturnValue(Path file) throws EngineException {
        try {
            Files.createDirectory(file);
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
