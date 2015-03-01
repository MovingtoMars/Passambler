package passambler.pkg.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueBool;
import passambler.value.ValueStr;

public class FunctionRead extends FunctionSimpleFile {
    @Override
    public Value getValue(Path file) {
        try {
            return new ValueStr(String.join("\n", Files.readAllLines(file)));
        } catch (Exception e) {
            return new ValueBool(false);
        }
    }
}
