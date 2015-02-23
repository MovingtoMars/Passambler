package passambler.pkg.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionDelete extends FunctionSimpleFile {
    @Override
    public Value getValue(Path file) {
        try {
            Files.delete(file);
        } catch (Exception e) {
            return new ValueBool(false);
        }

        return new ValueBool(true);
    }
}
