package passambler.bundle.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.Value;

public class DeleteFunction extends SimpleFileFunction {
    @Override
    public Value getReturnValue(Path file) throws EngineException {
        try {
            Files.delete(file);
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
