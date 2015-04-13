package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.StringValue;

public class TypeFunction extends SimpleFileFunction {
    @Override
    public Value getReturnValue(Path file) throws EngineException {
        try {
            return new StringValue(Files.probeContentType(file));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
