package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionCreate extends FunctionSimpleFile {
    @Override
    public Value getValue(Path file) {
        try {
            Files.createFile(file);
        } catch (Exception e) {
            return new ValueBool(false);
        }

        return new ValueBool(true);
    }
}
