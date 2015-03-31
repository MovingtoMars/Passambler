package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.ValueError;
import passambler.value.Value;
import passambler.value.ValueStr;

public class FunctionType extends FunctionSimpleFile {
    @Override
    public Value getReturnValue(Path file) {
        try {
            return new ValueStr(Files.probeContentType(file));
        } catch (Exception e) {
            return new ValueError(e);
        }
    }
}
