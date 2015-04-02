package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.exception.ErrorException;
import passambler.exception.EngineException;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionIsDir extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) throws EngineException {
        try {
            return new ValueBool(Files.isDirectory(file));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }
}
