package passambler.pkg.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionCreateDir extends FunctionSimpleFile {
    @Override
    public Value getValue(Path file) {
        try {
            Files.createDirectory(file);
        } catch (Exception e) {
            return new ValueBool(false);
        }

        return new ValueBool(true);
    }
}
