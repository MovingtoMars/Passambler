package passambler.module.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.Value;

public class TouchFunction extends SimpleFileFunction {
    @Override
    public Value getReturnValue(Path file) throws EngineException {
        try {
            Files.setLastModifiedTime(file, FileTime.fromMillis(System.currentTimeMillis()));
        } catch (Exception e) {
            throw new ErrorException(e);
        }

        return null;
    }
}
