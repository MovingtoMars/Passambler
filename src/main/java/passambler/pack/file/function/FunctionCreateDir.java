package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.ValueError;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionCreateDir extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) {
        try {
            Files.createDirectory(file);
        } catch (Exception e) {
            return new ValueError(e);
        }

        return new ValueBool(true);
    }
}
