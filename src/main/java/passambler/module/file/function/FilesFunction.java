package passambler.module.file.function;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.module.file.value.FileValue;
import passambler.value.ListValue;
import passambler.value.Value;

public class FilesFunction extends SimpleFileFunction {
    @Override
    public Value getReturnValue(Path file) throws EngineException {
        ListValue list = new ListValue();

        try {
            Files.newDirectoryStream(file).forEach(f -> list.getValue().add(new FileValue(f)));
        } catch (IOException e) {
            throw new ErrorException(e);
        }

        return list;
    }
}
