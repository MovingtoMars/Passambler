package passambler.pack.file.value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import passambler.exception.EngineException;
import passambler.exception.ErrorException;
import passambler.value.ReadableValue;
import passambler.value.StringValue;
import passambler.value.Value;
import passambler.value.WriteableValue;

public class FileValue extends Value implements ReadableValue, WriteableValue {
    public FileValue(Path file) {
        setValue(file);
    }

    @Override
    public Path getValue() {
        return (Path) value;
    }

    @Override
    public Value read() throws EngineException {
        try {
            return new StringValue(String.join("\n", Files.readAllLines(getValue())));
        } catch (Exception e) {
            throw new ErrorException(e);
        }
    }

    @Override
    public void write(Value value) throws EngineException {
        try {
            Files.write(getValue(), value.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ErrorException(e);
        }
    }
}
