package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueNum;

public class FunctionModified extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) {
        try {
            return new ValueNum(Files.getLastModifiedTime(file).toMillis());
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
