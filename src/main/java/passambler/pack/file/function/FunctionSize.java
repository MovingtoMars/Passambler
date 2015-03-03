package passambler.pack.file.function;

import java.nio.file.Files;
import java.nio.file.Path;
import passambler.value.Value;
import passambler.value.ValueNum;

public class FunctionSize extends FunctionSimpleFile {
    @Override
    public Value getValue(Path file) {
        try {
            return new ValueNum(Files.size(file));
        } catch (Exception e) {
            return new ValueNum(0);
        }
    }
}
