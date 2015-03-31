package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionExists extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) {
        try {
            return new ValueBool(Files.exists(file));
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
