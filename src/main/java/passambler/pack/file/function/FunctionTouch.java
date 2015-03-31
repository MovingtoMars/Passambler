package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import passambler.value.Value;
import passambler.value.ValueBool;

public class FunctionTouch extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) {
        try {
            Files.setLastModifiedTime(file, FileTime.fromMillis(System.currentTimeMillis()));
        } catch (Exception e) {
            return new ValueBool(false);
        }

        return new ValueBool(true);
    }
}
