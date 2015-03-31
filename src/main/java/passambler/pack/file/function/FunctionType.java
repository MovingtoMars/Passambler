package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueStr;

public class FunctionType extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) {
        try {
            return new ValueStr(Files.probeContentType(file));
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
