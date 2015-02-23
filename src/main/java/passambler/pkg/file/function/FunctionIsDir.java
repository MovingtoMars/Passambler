package passambler.pkg.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionIsDir extends FunctionSimpleFile {
    @Override
    public Value getValue(Path file) {
        try {
            return new ValueBool(Files.isDirectory(file));
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
